package org.cirrus.infrastructure.handler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import javax.inject.Inject;
import org.cirrus.infrastructure.handler.exception.CirrusException;
import org.cirrus.infrastructure.handler.exception.FailedEventSourceMappingException;
import org.cirrus.infrastructure.handler.exception.FailedMappingException;
import org.cirrus.infrastructure.handler.exception.FailedResourceCreationException;
import org.cirrus.infrastructure.handler.exception.FailedResourceDeletionException;
import org.cirrus.infrastructure.handler.exception.FailedStorageWriteException;
import org.cirrus.infrastructure.handler.model.FunctionConfig;
import org.cirrus.infrastructure.handler.model.NodeRecord;
import org.cirrus.infrastructure.handler.model.QueueConfig;
import org.cirrus.infrastructure.handler.service.FunctionService;
import org.cirrus.infrastructure.handler.service.QueueService;
import org.cirrus.infrastructure.handler.service.StorageService;
import org.cirrus.infrastructure.handler.util.Mapper;
import org.cirrus.infrastructure.handler.util.Resources;

public class CreateNodeCommand implements Command<CreateNodeRequest, CreateNodeResponse> {

  private final FunctionService functionService;
  private final QueueService queueService;
  private final StorageService<NodeRecord> storageService;
  private final Mapper mapper;

  @Inject
  public CreateNodeCommand(
      FunctionService functionService,
      QueueService queueService,
      StorageService<NodeRecord> storageService,
      Mapper mapper) {
    this.functionService = functionService;
    this.queueService = queueService;
    this.storageService = storageService;
    this.mapper = mapper;
  }

  /**
   * Creates a cloud-based node with computing and messaging capabilities.
   *
   * @param request A request that contains the identifier of the node and resource configuration.
   * @return A response containing the resource identifiers of the node.
   * @throws FailedResourceCreationException Thrown when any of the resources fail to be created.
   * @throws FailedResourceDeletionException Thrown when any of the created cloud resources fail to
   *     be deleted. During creation of the node, this is thrown when attempting to rollback after
   *     either (1) only some node resources could be successfully created or (2) any of the
   *     proceeding steps, after creating the node resources, fails.
   * @throws FailedEventSourceMappingException Thrown after successfully creating resources, but
   *     failing to add the node queue to the node function as an event source mapping. If, in the
   *     processing of rolling back, one of the created resources fails to be deleted, this
   *     exception is overridden by a {@link FailedResourceDeletionException}.
   * @throws FailedStorageWriteException Thrown after successfully creating resources and adding the
   *     queue as an event source mapping to the function, but failing to store the resource
   *     identifiers. If, in the processing of rolling back, one of the created resources fails to
   *     be deleted, this exception is overridden by a {@link FailedResourceDeletionException}.
   * @throws CirrusException Thrown when any unknown exception occurs.
   */
  public CreateNodeResponse run(CreateNodeRequest request) {
    try {
      return createNodeOrRollback(request)
          .thenComposeAsync(node -> attachQueueOrRollback(node, request.queueConfig()))
          .thenComposeAsync(this::saveRecordOrRollback)
          .thenApplyAsync(Node::toResponse)
          .join();
    } catch (CompletionException exception) {
      throw CirrusException.cast(exception.getCause());
    }
  }

  /**
   * @param input A JSON-formatted {@link CreateNodeRequest}
   * @return A JSON-formatted {@link CreateNodeResponse}
   * @throws FailedMappingException Thrown when the input fails to be converted into a {@link
   *     CreateNodeRequest} instance, or the output fails to be converted into a {@link
   *     CreateNodeResponse} instance.
   * @see CreateNodeCommand#run(CreateNodeRequest)
   */
  public String runFromString(String input) {
    CreateNodeRequest request = mapper.read(input, CreateNodeRequest.class);
    CreateNodeResponse response = run(request);
    return mapper.write(response);
  }

  private CompletableFuture<Resource> createFunction(FunctionConfig config) {
    return functionService.createFunction(config).handleAsync(Resource::new);
  }

  private CompletableFuture<Resource> createQueue(QueueConfig config) {
    return queueService.createQueue(config).handleAsync(Resource::new);
  }

  private CompletableFuture<Node> createNodeOrRollback(CreateNodeRequest request) {
    FunctionConfig config = request.functionConfig();
    String nodeId = Resources.createRandomId();
    return createFunction(config)
        .thenCombineAsync(
            createQueue(request.queueConfig()),
            (function, queue) -> new Node(nodeId, config.artifactId(), function, queue))
        .thenComposeAsync(this::orPartialRollback);
  }

  private CompletableFuture<Node> orPartialRollback(Node node) {
    CompletableFuture<?> result = CompletableFuture.completedFuture(node);
    Resource function = node.function;
    Resource queue = node.queue;
    if (function.failed() && queue.succeeded()) {
      result = deleteQueue(node).thenRunAsync(() -> throwException(function.throwable));
    } else if (function.succeeded() && queue.failed()) {
      result = deleteFunction(node).thenRunAsync(() -> throwException(queue.throwable));
    } else if (function.failed() && queue.failed()) {
      result = CompletableFuture.failedFuture(new FailedResourceCreationException());
    }
    return result.thenApplyAsync(x -> node);
  }

  private void throwException(Throwable throwable) {
    throw (RuntimeException) throwable;
  }

  private CompletableFuture<Node> attachQueueOrRollback(Node node, QueueConfig config) {
    return functionService
        .attachQueue(node.function.id, node.queue.id, config)
        .handleAsync((x, throwable) -> throwable)
        .thenComposeAsync(throwable -> orCompleteRollback(node, throwable));
  }

  private CompletableFuture<Node> saveRecordOrRollback(Node node) {
    return storageService
        .putItem(node.toRecord())
        .handleAsync((x, throwable) -> throwable)
        .thenComposeAsync(throwable -> orCompleteRollback(node, throwable));
  }

  private CompletableFuture<Node> orCompleteRollback(Node node, Throwable throwable) {
    CompletableFuture<?> result = CompletableFuture.completedFuture(node);
    if (throwable != null) {
      Runnable throwException = () -> throwException(throwable);
      result = deleteFunction(node).runAfterBothAsync(deleteQueue(node), throwException);
    }
    return result.thenApplyAsync(x -> node);
  }

  private CompletableFuture<?> deleteQueue(Node node) {
    return queueService.deleteQueue(node.queue.id);
  }

  private CompletableFuture<?> deleteFunction(Node node) {
    return functionService.deleteFunction(node.function.id);
  }

  private static final class Node {

    private final String nodeId;
    private final String artifactId;
    private final Resource function;
    private final Resource queue;

    private Node(String nodeId, String artifactId, Resource function, Resource queue) {
      this.nodeId = nodeId;
      this.artifactId = artifactId;
      this.function = function;
      this.queue = queue;
    }

    public NodeRecord toRecord() {
      return NodeRecord.builder()
          .nodeId(nodeId)
          .functionId(function.id)
          .queueId(queue.id)
          .artifactId(artifactId)
          .build();
    }

    public CreateNodeResponse toResponse() {
      return CreateNodeResponse.builder()
          .nodeId(nodeId)
          .functionId(function.id)
          .queueId(queue.id)
          .artifactId(artifactId)
          .build();
    }
  }

  private static final class Resource {

    private final String id;
    private final Throwable throwable;

    private Resource(@Nullable String id, @Nullable Throwable throwable) {
      this.id = id;
      this.throwable = throwable;
    }

    public boolean failed() {
      return id == null && throwable != null;
    }

    public boolean succeeded() {
      return id != null && throwable == null;
    }
  }
}
