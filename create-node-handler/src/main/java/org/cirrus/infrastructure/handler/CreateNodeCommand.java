package org.cirrus.infrastructure.handler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.inject.Inject;
import org.cirrus.infrastructure.handler.exception.FailedEventSourceMappingException;
import org.cirrus.infrastructure.handler.exception.FailedResourceCreationException;
import org.cirrus.infrastructure.handler.exception.FailedResourceDeletionException;
import org.cirrus.infrastructure.handler.exception.FailedStorageReadException;
import org.cirrus.infrastructure.handler.exception.FailedStorageWriteException;
import org.cirrus.infrastructure.handler.exception.NodeAlreadyExistsException;
import org.cirrus.infrastructure.handler.model.FunctionConfig;
import org.cirrus.infrastructure.handler.model.NodeRecord;
import org.cirrus.infrastructure.handler.model.QueueConfig;
import org.cirrus.infrastructure.handler.model.Resource;
import org.cirrus.infrastructure.handler.service.FunctionService;
import org.cirrus.infrastructure.handler.service.QueueService;
import org.cirrus.infrastructure.handler.service.StorageService;
import org.cirrus.infrastructure.handler.util.Mapper;

final class CreateNodeCommand implements Command<CreateNodeRequest, CreateNodeResponse> {

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
   * @param request Contains the identifier of the node and resource configuration.
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
   * @return A response containing the resource identifiers of the node.
   */
  public CreateNodeResponse run(CreateNodeRequest request) {
    try {
      CompletableFuture<Node> createNode =
          checkIfNodeExists(request.nodeId()).thenComposeAsync(x -> createNode(request));
      CompletableFuture<Void> attachQueueThenSaveRecord =
          attachQueueThenSaveRecord(createNode, request.queueConfig());
      return getResponse(createNode, attachQueueThenSaveRecord);
    } catch (CompletionException exception) {
      throw (RuntimeException) exception.getCause();
    }
  }

  /**
   * @param request JSON-formatted {@link CreateNodeRequest}
   * @return JSON-formatted {@link CreateNodeResponse}
   * @see CreateNodeCommand#run(CreateNodeRequest)
   */
  public String runFromString(String request) {
    return mapToOutput(run(mapToInput(request)));
  }

  private CompletableFuture<Void> checkIfNodeExists(String nodeId) {
    return storageService.getItem(nodeId).thenApplyAsync(this::throwIfPresent);
  }

  private <T> T throwIfPresent(Object response) {
    if (response != null) {
      throw new NodeAlreadyExistsException();
    }
    return null;
  }

  private CompletableFuture<Void> attachQueueThenSaveRecord(
      CompletableFuture<Node> createNode, QueueConfig config) {
    return createNode
        .thenComposeAsync(node -> attachQueue(node, config))
        .thenComposeAsync(this::saveRecord);
  }

  private CreateNodeResponse getResponse(
      CompletableFuture<Node> createNode, CompletableFuture<Void> attachQueueThenSaveRecord) {
    return createNode
        .thenCombineAsync(attachQueueThenSaveRecord, (node, x) -> mapToResponse(node))
        .join();
  }

  private String mapToOutput(CreateNodeResponse response) {
    return mapper.write(response);
  }

  private CreateNodeRequest mapToInput(String data) {
    return mapper.read(data, CreateNodeRequest.class);
  }

  private CompletableFuture<Resource> createFunction(FunctionConfig config) {
    return functionService.createFunction(config);
  }

  private CompletableFuture<Resource> createQueue(QueueConfig config) {
    return queueService.createQueue(config);
  }

  private CompletableFuture<Node> createNode(CreateNodeRequest request) {
    FunctionConfig fConfig = request.functionConfig();
    QueueConfig qConfig = request.queueConfig();
    String nodeId = request.nodeId();
    return createFunction(fConfig)
        .thenCombineAsync(createQueue(qConfig), (func, queue) -> new Node(nodeId, func, queue))
        .thenComposeAsync(this::orPartialRollback);
  }

  private CompletableFuture<Node> orPartialRollback(Node node) {
    CompletableFuture<?> result;
    Resource function = node.function;
    Resource queue = node.queue;
    if (function.failed() && queue.succeeded()) {
      result = deleteQueue(queue.id()).thenRunAsync(() -> throwException(function.exception()));
    } else if (function.succeeded() && queue.failed()) {
      result = deleteFunction(function.id()).thenRunAsync(() -> throwException(queue.exception()));
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

  private CompletableFuture<Node> attachQueue(Node node, QueueConfig config) {
    return attachQueue(node.function.id(), node.queue.id(), config)
        .thenComposeAsync(throwable -> orCompleteRollback(node, throwable))
        .thenApplyAsync(x -> node);
  }

  private CompletableFuture<Void> saveRecord(Node node) {
    return saveRecord(node.nodeId, node.function.id(), node.queue.id())
        .thenComposeAsync(throwable -> orCompleteRollback(node, throwable))
        .thenApplyAsync(x -> null);
  }

  private CompletableFuture<?> orCompleteRollback(Node node, Throwable throwable) {
    CompletableFuture<?> result;
    if (throwable == null) {
      result = CompletableFuture.completedFuture(node);
    } else {
      CompletableFuture<?> deleteFunction = deleteFunction(node.function.id());
      CompletableFuture<?> deleteQueue = deleteQueue(node.queue.id());
      result = deleteFunction.runAfterBothAsync(deleteQueue, () -> throwException(throwable));
    }
    return result;
  }

  private CreateNodeResponse mapToResponse(Node node) {
    return CreateNodeResponse.builder()
        .nodeId(node.nodeId)
        .functionId(node.function.id())
        .queueId(node.queue.id())
        .build();
  }

  private CompletableFuture<?> deleteQueue(String queueId) {
    return queueService.deleteQueue(queueId);
  }

  private CompletableFuture<?> deleteFunction(String functionId) {
    return functionService.deleteFunction(functionId);
  }

  private CompletableFuture<Throwable> attachQueue(
      String functionId, String queueId, QueueConfig config) {
    return functionService
        .attachQueue(functionId, queueId, config)
        .handleAsync((x, throwable) -> throwable);
  }

  private CompletableFuture<Throwable> saveRecord(
      String nodeId, String functionId, String queueId) {
    return storageService
        .putItem(createNodeRecord(nodeId, functionId, queueId))
        .handleAsync((x, throwable) -> throwable);
  }

  private NodeRecord createNodeRecord(String nodeId, String functionId, String queueId) {
    return NodeRecord.builder().nodeId(nodeId).functionId(functionId).queueId(queueId).build();
  }

  private static class Node {

    private final String nodeId;
    private final Resource function;
    private final Resource queue;

    private Node(String nodeId, Resource function, Resource queue) {
      this.nodeId = nodeId;
      this.function = function;
      this.queue = queue;
    }
  }
}
