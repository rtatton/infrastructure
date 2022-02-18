package org.cirrus.infrastructure.handler;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
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
import org.cirrus.infrastructure.util.Logger;
import org.cirrus.infrastructure.util.Mapper;
import org.cirrus.infrastructure.util.ResourceUtil;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.CreateFunctionResponse;
import software.amazon.awssdk.services.lambda.model.FunctionCode;
import software.amazon.awssdk.services.lambda.model.PackageType;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

@Singleton
public class CreateNodeCommand {
  private static final CreateNodeComponent COMPONENT = DaggerCreateNodeComponent.create();
  private static final String TRUE = "true";
  private final LambdaAsyncClient lambdaClient;
  private final SqsAsyncClient sqsClient;
  private final DynamoDbAsyncTable<NodeRecord> nodeRegistry;
  private final Mapper mapper;
  private final Logger logger;

  @Inject
  CreateNodeCommand(
      LambdaAsyncClient lambdaClient,
      SqsAsyncClient sqsClient,
      DynamoDbAsyncTable<NodeRecord> nodeRegistry,
      Mapper mapper,
      Logger logger) {
    this.lambdaClient = lambdaClient;
    this.sqsClient = sqsClient;
    this.nodeRegistry = nodeRegistry;
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

  private String mapToOutput(CreateNodeResponse response) {
    return mapper.write(response, logger);
  }

  private CreateNodeRequest mapToInput(String data) {
    return mapper.read(data, CreateNodeRequest.class, logger);
  }

  private CompletionStage<Resource> createFunction(FunctionConfig config) {
    CompletionStage<CreateFunctionResponse> response =
        lambdaClient.createFunction(
            builder ->
                builder
                    .functionName(ResourceUtil.createRandomId())
                    .packageType(PackageType.ZIP)
                    .code(getFunctionCode(config))
                    .runtime(config.runtime())
                    .handler(config.handlerName())
                    .memorySize(config.memorySizeMegabytes())
                    .timeout(config.timeoutSeconds())
                    .role("") // TODO
                    .publish(true));
    return createResource(response, CreateFunctionResponse::functionArn);
  }

  private CompletionStage<Resource> createQueue(QueueConfig config) {
    String queueId = ResourceUtil.createRandomId();
    Map<QueueAttributeName, String> props = getQueueProps(config);
    CompletionStage<CreateQueueResponse> response =
        sqsClient.createQueue(builder -> builder.queueName(queueId).attributes(props));
    return createResource(response, CreateQueueResponse::queueUrl);
  }

  private CompletionStage<?> orPartialRollback(Props props, Throwable throwable) {
    CompletionStage<?> result = CompletableFuture.completedFuture(props);
    if (throwable != null) {
      String functionId = props.function.id;
      String queueId = props.queue.id;
      result = functionId == null ? deleteQueue(queueId) : deleteFunction(functionId);
      result = result.handleAsync(this::throwRuntimeException);
    }
    return result;
  }

  private CreateNodeResponse mapToResponse(Props props) {
    return CreateNodeResponse.builder()
        .nodeId(props.nodeId)
        .functionId(props.function.id)
        .queueId(props.queue.id)
        .build();
  }

  private FunctionCode getFunctionCode(FunctionConfig config) {
    return FunctionCode.builder().s3Bucket(config.codeBucket()).s3Key(config.codeKey()).build();
  }

  private <T> CompletionStage<Resource> createResource(
      CompletionStage<T> stage, Function<T, String> getId) {
    return stage.handleAsync(
        (response, throwable) -> {
          Resource resource;
          if (throwable == null) {
            resource = new Resource(getId.apply(response));
          } else {
            logger.error(throwable.getLocalizedMessage());
            resource = new Resource(new FailedResourceCreationException(throwable));
          }
          return resource;
        });
  }

  private Map<QueueAttributeName, String> getQueueProps(QueueConfig config) {
    return Map.of(
        QueueAttributeName.DELAY_SECONDS,
        String.valueOf(config.delaySeconds()),
        QueueAttributeName.MAXIMUM_MESSAGE_SIZE,
        String.valueOf(config.maxMessageSizeBytes()),
        QueueAttributeName.MESSAGE_RETENTION_PERIOD,
        String.valueOf(config.messageRetentionPeriodSeconds()),
        QueueAttributeName.POLICY,
        "", // TODO
        QueueAttributeName.RECEIVE_MESSAGE_WAIT_TIME_SECONDS,
        String.valueOf(config.receiveMessageWaitTimeSeconds()),
        QueueAttributeName.VISIBILITY_TIMEOUT,
        String.valueOf(config.visibilityTimeoutSeconds()),
        QueueAttributeName.FIFO_QUEUE,
        TRUE,
        QueueAttributeName.CONTENT_BASED_DEDUPLICATION,
        TRUE);
  }

  private CompletionStage<Props> throwIfFailed(Props props) {
    if (props.function.id != null && props.queue.id != null) {
      return CompletableFuture.completedFuture(props);
    } else if (props.function.exception != null) {
      throw props.function.exception;
    } else {
      throw props.queue.exception;
    }
  }

  private CompletionStage<?> deleteQueue(String queueId) {
    return wrapThrowable(
        sqsClient.deleteQueue(builder -> builder.queueUrl(queueId)),
        FailedResourceDeletionException::new);
  }

  private CompletionStage<?> deleteFunction(String functionId) {
    return wrapThrowable(
        lambdaClient.deleteFunction(builder -> builder.functionName(functionId)),
        FailedResourceDeletionException::new);
  }

  private <T, E extends RuntimeException> CompletionStage<T> wrapThrowable(
      CompletionStage<T> stage, Function<Throwable, E> mapToRuntimeException) {
    return stage.handleAsync(
        (response, throwable) -> {
          if (throwable != null) {
            logger.error(throwable.getLocalizedMessage());
            throw mapToRuntimeException.apply(throwable);
          }
          return response;
        });
  }

  private CompletionStage<Props> addQueueThenStoreIds(Props props, QueueConfig config) {
    return addQueue(props, config)
        .thenComposeAsync(x -> putItem(props.nodeId, props.function.id, props.queue.id))
        .thenComposeAsync(x -> CompletableFuture.completedFuture(props));
  }

  private CompletionStage<?> addQueue(Props props, QueueConfig config) {
    return wrapThrowable(
        lambdaClient.createEventSourceMapping(
            builder ->
                builder
                    .functionName(props.function.id)
                    .eventSourceArn(props.queue.id)
                    .batchSize(config.batchSize())),
        FailedEventSourceMappingException::new);
  }

  private CompletionStage<?> putItem(String nodeId, String functionId, String queueId) {
    return wrapThrowable(
        nodeRegistry.putItem(
            NodeRecord.builder().nodeId(nodeId).functionId(functionId).queueId(queueId).build()),
        FailedStorageWriteException::new);
  }

  private CompletionStage<?> orCompleteRollback(Props props, Throwable throwable) {
    CompletionStage<?> result = CompletableFuture.completedFuture(props);
    if (throwable != null) {
      result =
          deleteQueue(props.queue.id)
              .handleAsync((r, t) -> deleteFunction(props.function.id))
              .handleAsync(this::throwRuntimeException);
    }
    return result;
  }

  private <T> T throwRuntimeException(T result, Throwable throwable) {
    throw (RuntimeException) throwable;
  }

  private static class Resource {
    private final String id;
    private final RuntimeException exception;

    private Resource(RuntimeException exception) {
      this(null, exception);
    }

    private Resource(String id, RuntimeException exception) {
      this.id = id;
      this.exception = exception;
    }

    private Resource(String id) {
      this(id, null);
    }
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
