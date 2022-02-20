package org.cirrus.infrastructure.handler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.cirrus.infrastructure.handler.exception.FailedEventSourceMappingException;
import org.cirrus.infrastructure.handler.exception.FailedResourceCreationException;
import org.cirrus.infrastructure.handler.exception.FailedResourceDeletionException;
import org.cirrus.infrastructure.handler.exception.FailedStorageWriteException;
import org.cirrus.infrastructure.handler.model.CreateNodeRequest;
import org.cirrus.infrastructure.handler.model.CreateNodeResponse;
import org.cirrus.infrastructure.handler.model.FunctionConfig;
import org.cirrus.infrastructure.handler.model.NodeRecord;
import org.cirrus.infrastructure.handler.model.QueueConfig;
import org.cirrus.infrastructure.handler.model.Resource;
import org.cirrus.infrastructure.handler.service.FunctionService;
import org.cirrus.infrastructure.handler.service.QueueService;
import org.cirrus.infrastructure.handler.service.StorageService;
import org.cirrus.infrastructure.util.Mapper;

@Singleton
public class CreateNodeCommand {

  private static final CreateNodeComponent COMPONENT = DaggerCreateNodeComponent.create();
  private final FunctionService functionService;
  private final QueueService queueService;
  private final StorageService<NodeRecord> storageService;
  private final Mapper mapper;

  @Inject
  CreateNodeCommand(
      FunctionService functionService,
      QueueService queueService,
      StorageService<NodeRecord> storageService,
      Mapper mapper) {
    this.functionService = functionService;
    this.queueService = queueService;
    this.storageService = storageService;
    this.mapper = mapper;
  }

  public static CreateNodeCommand getInstance() {
    return COMPONENT.getCommand();
  }

  /**
   * @param request JSON-formatted {@link CreateNodeRequest}
   * @return JSON-formatted {@link CreateNodeResponse}
   * @see CreateNodeCommand#run(CreateNodeRequest)
   */
  public String runFromString(String request) {
    return mapToOutput(run(mapToInput(request)));
  }

  /**
   * Creates a cloud-based node with computing and messaging capabilities.
   *
   * @param request Contains the identifier of the node and resource configuration.
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
      CompletionStage<Resources> createResources = createResources(request);
      CompletionStage<Void> attachQueueThenSaveRecord =
          attachQueueThenSaveRecord(createResources, request.queueConfig());
      return getResponse(createResources, attachQueueThenSaveRecord);
    } catch (CompletionException exception) {
      throw (RuntimeException) exception.getCause();
    }
  }

  private CompletionStage<Void> attachQueueThenSaveRecord(
      CompletionStage<Resources> createResources, QueueConfig config) {
    return createResources
        .thenComposeAsync(resources -> attachQueue(resources, config))
        .thenComposeAsync(this::saveRecord);
  }

  private CreateNodeResponse getResponse(
      CompletionStage<Resources> createResources, CompletionStage<Void> attachQueueThenSaveRecord) {
    return createResources
        .thenCombineAsync(attachQueueThenSaveRecord, (resources, x) -> mapToResponse(resources))
        .toCompletableFuture()
        .join();
  }

  private String mapToOutput(CreateNodeResponse response) {
    return mapper.write(response);
  }

  private CreateNodeRequest mapToInput(String data) {
    return mapper.read(data, CreateNodeRequest.class);
  }

  private CompletionStage<Resource> createFunction(FunctionConfig config) {
    return functionService.createFunction(config);
  }

  private CompletionStage<Resource> createQueue(QueueConfig config) {
    return queueService.createQueue(config);
  }

  private CompletionStage<Resources> createResources(CreateNodeRequest request) {
    FunctionConfig fConfig = request.functionConfig();
    QueueConfig qConfig = request.queueConfig();
    String nodeId = request.nodeId();
    return createFunction(fConfig)
        .thenCombineAsync(createQueue(qConfig), (func, queue) -> new Resources(nodeId, func, queue))
        .thenComposeAsync(this::orPartialRollback);
  }

  private CompletionStage<Resources> orPartialRollback(Resources resources) {
    CompletionStage<?> result;
    Resource function = resources.function;
    Resource queue = resources.queue;
    if (function.failed() && queue.succeeded()) {
      result = deleteQueue(queue.id()).thenRunAsync(() -> throwException(function.exception()));
    } else if (function.succeeded() && queue.failed()) {
      result = deleteFunction(function.id()).thenRunAsync(() -> throwException(queue.exception()));
    } else if (function.failed() && queue.failed()) {
      result = CompletableFuture.failedFuture(new FailedResourceCreationException());
    } else {
      result = CompletableFuture.completedFuture(resources);
    }
    return result.thenApplyAsync(x -> resources);
  }

  private void throwException(Throwable throwable) {
    throw (RuntimeException) throwable;
  }

  private CompletionStage<Resources> attachQueue(Resources resources, QueueConfig config) {
    return attachQueue(resources.function.id(), resources.queue.id(), config)
        .thenComposeAsync(error -> orCompleteRollback(resources, error))
        .thenApplyAsync(x -> resources);
  }

  private CompletionStage<Void> saveRecord(Resources resources) {
    return saveRecord(resources.nodeId, resources.function.id(), resources.queue.id())
        .thenComposeAsync(error -> orCompleteRollback(resources, error))
        .thenApplyAsync(x -> null);
  }

  private CompletionStage<?> orCompleteRollback(Resources resources, Error error) {
    CompletionStage<?> result;
    Throwable throwable = error.throwable;
    if (throwable == null) {
      result = CompletableFuture.completedFuture(resources);
    } else {
      CompletionStage<?> deleteFunction = deleteFunction(resources.function.id());
      CompletionStage<?> deleteQueue = deleteQueue(resources.queue.id());
      result = deleteFunction.runAfterBothAsync(deleteQueue, () -> throwException(throwable));
    }
    return result;
  }

  private CreateNodeResponse mapToResponse(Resources resources) {
    return CreateNodeResponse.builder()
        .nodeId(resources.nodeId)
        .functionId(resources.function.id())
        .queueId(resources.queue.id())
        .build();
  }

  private CompletionStage<?> deleteQueue(String queueId) {
    return queueService.deleteQueue(queueId);
  }

  private CompletionStage<?> deleteFunction(String functionId) {
    return functionService.deleteFunction(functionId);
  }

  private CompletionStage<Error> attachQueue(
      String functionId, String queueId, QueueConfig config) {
    return functionService
        .attachQueue(functionId, queueId, config)
        .handleAsync((x, throwable) -> new Error(throwable));
  }

  private CompletionStage<Error> saveRecord(String nodeId, String functionId, String queueId) {
    return storageService
        .put(createNodeRecord(nodeId, functionId, queueId))
        .handleAsync((x, throwable) -> new Error(throwable));
  }

  private NodeRecord createNodeRecord(String nodeId, String functionId, String queueId) {
    return NodeRecord.builder().nodeId(nodeId).functionId(functionId).queueId(queueId).build();
  }

  private static class Resources {

    private final String nodeId;
    private final Resource function;
    private final Resource queue;

    private Resources(String nodeId, Resource function, Resource queue) {
      this.nodeId = nodeId;
      this.function = function;
      this.queue = queue;
    }
  }

  private static class Error {
    private final Throwable throwable;

    private Error(Throwable throwable) {
      this.throwable = throwable;
    }
  }
}
