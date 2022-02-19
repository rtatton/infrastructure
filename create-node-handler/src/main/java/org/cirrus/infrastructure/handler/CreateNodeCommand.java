package org.cirrus.infrastructure.handler;

import java.util.concurrent.CompletableFuture;
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
import org.cirrus.infrastructure.util.Logger;
import org.cirrus.infrastructure.util.Mapper;

@Singleton
public class CreateNodeCommand {

  private static final CreateNodeComponent COMPONENT = DaggerCreateNodeComponent.create();
  private final FunctionService functionService;
  private final QueueService queueService;
  private final StorageService<NodeRecord> storageService;
  private final Mapper mapper;
  private final Logger logger;

  @Inject
  CreateNodeCommand(
      FunctionService functionService,
      QueueService queueService,
      StorageService<NodeRecord> storageService,
      Mapper mapper,
      Logger logger) {
    this.functionService = functionService;
    this.queueService = queueService;
    this.storageService = storageService;
    this.mapper = mapper;
    this.logger = logger;
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

  private String mapToOutput(CreateNodeResponse response) {
    return mapper.write(response, logger);
  }

  /**
   * Creates a cloud-based node with computing and messaging capabilities.
   *
   * @param request Contains the identifier of the node and resource configuration.
   * @throws FailedResourceCreationException Thrown when any of the node resources fail to be
   *     created. When only some node resources are created successfully, this exception is thrown.
   * @throws FailedResourceDeletionException Thrown when any of the created cloud resources fail to
   *     be deleted. During creation of the node, this is thrown when attempting to rollback after
   *     either (1) only some node resources could be successfully created or (2) any of the
   *     proceeding steps, after creating the node resources, fails.
   * @throws FailedEventSourceMappingException Thrown after successfully creating node resources,
   *     but failing to add the node queue to the node function as an event source mapping.
   * @throws FailedStorageWriteException Thrown after successfully creating node resources and
   *     adding the node queue as an event source mapping to the node function, but failing to store
   *     the node resource identifiers.
   * @return A response containing the resource identifiers of the node.
   */
  public CreateNodeResponse run(CreateNodeRequest request) {
    FunctionConfig fConfig = request.functionConfig();
    QueueConfig qConfig = request.queueConfig();
    String nodeId = request.nodeId();
    CompletionStage<Props> getProps =
        createFunction(fConfig)
            .thenCombineAsync(createQueue(qConfig), (func, queue) -> new Props(nodeId, func, queue))
            .thenComposeAsync(this::throwIfFailed);
    return getProps
        .handleAsync(this::orPartialRollback)
        .thenComposeAsync(x -> getProps)
        .thenComposeAsync(props -> addQueueThenStoreIds(props, qConfig))
        .handleAsync(this::orCompleteRollback)
        .thenComposeAsync(x -> getProps)
        .thenApplyAsync(this::mapToResponse)
        .toCompletableFuture()
        .join();
  }

  private CreateNodeRequest mapToInput(String data) {
    return mapper.read(data, CreateNodeRequest.class, logger);
  }

  private CompletionStage<Resource> createFunction(FunctionConfig config) {
    return functionService.createFunction(config);
  }

  private CompletionStage<Resource> createQueue(QueueConfig config) {
    return queueService.createQueue(config);
  }

  @SuppressWarnings("ConstantConditions") // Ignore nullable exceptions warning
  private CompletionStage<Props> throwIfFailed(Props props) {
    if (props.function.id() != null && props.queue.id() != null) {
      return CompletableFuture.completedFuture(props);
    } else if (props.function.exception() != null) {
      throw props.function.exception();
    } else {
      throw props.queue.exception();
    }
  }

  private CompletionStage<?> orPartialRollback(Props props, Throwable throwable) {
    CompletionStage<?> result = CompletableFuture.completedFuture(props);
    if (throwable != null) {
      String functionId = props.function.id();
      String queueId = props.queue.id();
      result = functionId == null ? deleteQueue(queueId) : deleteFunction(functionId);
      result = result.handleAsync(this::throwRuntimeException);
    }
    return result;
  }

  private CompletionStage<?> deleteQueue(String queueId) {
    return queueService.deleteQueue(queueId);
  }

  private CompletionStage<?> deleteFunction(String functionId) {
    return functionService.deleteFunction(functionId);
  }

  private CompletionStage<Props> addQueueThenStoreIds(Props props, QueueConfig config) {
    return addQueue(props, config)
        .thenComposeAsync(x -> saveRecord(props.nodeId, props.function.id(), props.queue.id()))
        .thenComposeAsync(x -> CompletableFuture.completedFuture(props));
  }

  private CompletionStage<?> addQueue(Props props, QueueConfig config) {
    return functionService.attachQueue(props.function.id(), props.queue.id(), config);
  }

  private CompletionStage<?> saveRecord(String nodeId, String functionId, String queueId) {
    return storageService.put(
        NodeRecord.builder().nodeId(nodeId).functionId(functionId).queueId(queueId).build());
  }

  private <T> T throwRuntimeException(T result, Throwable throwable) {
    throw (RuntimeException) throwable;
  }

  private CompletionStage<?> orCompleteRollback(Props props, Throwable throwable) {
    CompletionStage<?> result = CompletableFuture.completedFuture(props);
    if (throwable != null) {
      result =
          deleteQueue(props.queue.id())
              .handleAsync((r, t) -> deleteFunction(props.function.id()))
              .handleAsync(this::throwRuntimeException);
    }
    return result;
  }

  private CreateNodeResponse mapToResponse(Props props) {
    return CreateNodeResponse.builder()
        .nodeId(props.nodeId)
        .functionId(props.function.id())
        .queueId(props.queue.id())
        .build();
  }

  private static class Props {

    private final String nodeId;
    private final Resource function;
    private final Resource queue;

    private Props(String nodeId, Resource function, Resource queue) {
      this.nodeId = nodeId;
      this.function = function;
      this.queue = queue;
    }
  }
}
