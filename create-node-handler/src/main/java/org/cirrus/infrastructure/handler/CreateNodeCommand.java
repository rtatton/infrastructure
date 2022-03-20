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
import org.cirrus.infrastructure.handler.exception.FailedStorageReadException;
import org.cirrus.infrastructure.handler.exception.FailedStorageWriteException;
import org.cirrus.infrastructure.handler.exception.NodeAlreadyExistsException;
import org.cirrus.infrastructure.handler.model.FunctionConfig;
import org.cirrus.infrastructure.handler.model.NodeRecord;
import org.cirrus.infrastructure.handler.model.QueueConfig;
import org.cirrus.infrastructure.handler.service.FunctionService;
import org.cirrus.infrastructure.handler.service.QueueService;
import org.cirrus.infrastructure.handler.service.StorageService;
import org.cirrus.infrastructure.handler.util.Mapper;

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

  private static CreateNodeResponse mapToResponse(Node node) {
    return CreateNodeResponse.builder()
        .nodeId(node.nodeId)
        .functionId(node.function.id)
        .queueId(node.queue.id)
        .build();
  }

  private static <T> T throwIfPresent(Object response) {
    if (response != null) {
      throw new NodeAlreadyExistsException();
    }
    return null;
  }

  /**
   * Creates a cloud-based node with computing and messaging capabilities.
   *
   * @param request A request that contains the identifier of the node and resource configuration.
   * @return A response containing the resource identifiers of the node.
   * @throws NodeAlreadyExistsException Thrown when the requested node identifier already exists.
   * @throws FailedStorageReadException Thrown when an error occurs when attempting to access the
   *     storage service to check if the requested node identifier already exists.
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
      return checkIfNodeExists(request.nodeId())
          .thenComposeAsync(x -> createNodeOrRollback(request))
          .thenComposeAsync(node -> attachQueueOrRollback(node, request.queueConfig()))
          .thenComposeAsync(this::saveRecordOrRollback)
          .thenApplyAsync(CreateNodeCommand::mapToResponse)
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

  private CompletableFuture<Void> checkIfNodeExists(String nodeId) {
    return storageService.getItem(nodeId).thenApplyAsync(CreateNodeCommand::throwIfPresent);
  }

  private CompletableFuture<Resource> createFunction(FunctionConfig config) {
    return functionService.createFunction(config).handleAsync(Resource::new);
  }

  private CompletableFuture<Resource> createQueue(QueueConfig config) {
    return queueService.createQueue(config).handleAsync(Resource::new);
  }

  private CompletableFuture<Node> createNodeOrRollback(CreateNodeRequest request) {
    FunctionConfig functionConfig = request.functionConfig();
    String codeId = functionConfig.artifactId();
    return createFunction(functionConfig)
        .thenCombineAsync(
            createQueue(request.queueConfig()),
            (function, queue) -> new Node(request.nodeId(), codeId, function, queue))
        .thenComposeAsync(this::orPartialRollback);
  }

  private CompletableFuture<Node> orPartialRollback(Node node) {
    CompletableFuture<?> result;
    Resource function = node.function;
    Resource queue = node.queue;
    if (function.failed() && queue.succeeded()) {
      result = deleteQueue(queue.id).thenRunAsync(() -> throwException(function.throwable));
    } else if (function.succeeded() && queue.failed()) {
      result = deleteFunction(function.id).thenRunAsync(() -> throwException(queue.throwable));
    } else if (function.failed() && queue.failed()) {
      result = CompletableFuture.failedFuture(new FailedResourceCreationException());
    } else {
      result = CompletableFuture.completedFuture(node);
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
        .thenComposeAsync(throwable -> orCompleteRollback(node, throwable))
        .thenApplyAsync(x -> node);
  }

  private CompletableFuture<Node> saveRecordOrRollback(Node node) {
    return storageService
        .putItem(
            NodeRecord.builder()
                .nodeId(node.nodeId)
                .functionId(node.function.id)
                .queueId(node.queue.id)
                .codeId(node.codeId)
                .build())
        .handleAsync((x, throwable) -> throwable)
        .thenComposeAsync(throwable -> orCompleteRollback(node, throwable))
        .thenApplyAsync(x -> node);
  }

  private CompletableFuture<?> orCompleteRollback(Node node, Throwable throwable) {
    CompletableFuture<?> result;
    if (throwable == null) {
      result = CompletableFuture.completedFuture(node);
    } else {
      CompletableFuture<?> deleteFunction = deleteFunction(node.function.id);
      CompletableFuture<?> deleteQueue = deleteQueue(node.queue.id);
      result = deleteFunction.runAfterBothAsync(deleteQueue, () -> throwException(throwable));
    }
    return result;
  }

  private CompletableFuture<?> deleteQueue(String queueId) {
    return queueService.deleteQueue(queueId);
  }

  private CompletableFuture<?> deleteFunction(String functionId) {
    return functionService.deleteFunction(functionId);
  }

  private static final class Node {

    private final String nodeId;
    private final String codeId;
    private final Resource function;
    private final Resource queue;

    private Node(String nodeId, String codeId, Resource function, Resource queue) {
      this.nodeId = nodeId;
      this.codeId = codeId;
      this.function = function;
      this.queue = queue;
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
