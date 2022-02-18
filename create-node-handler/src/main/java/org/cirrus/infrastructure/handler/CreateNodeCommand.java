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
      LambdaAsyncClient lambda,
      SqsAsyncClient sqs,
      DynamoDbAsyncTable<NodeRecord> nodeRegistry,
      Mapper mapper,
      Logger logger) {
    this.lambdaClient = lambda;
    this.sqsClient = sqs;
    this.nodeRegistry = nodeRegistry;
    this.mapper = mapper;
    this.logger = logger;
  }

  public static CreateNodeCommand getInstance() {
    return COMPONENT.getCommand();
  }

  public String runFromString(String request) {
    return mapToOutput(run(mapToInput(request)));
  }

  public CreateNodeResponse run(CreateNodeRequest request) {
    FunctionConfig fConfig = request.getFunctionConfig();
    QueueConfig qConfig = request.getQueueConfig();
    String nodeId = request.getNodeId();
    CompletionStage<Props> getProps =
        createFunction(fConfig)
            .thenCombineAsync(createQueue(qConfig), (func, queue) -> new Props(nodeId, func, queue))
            .thenComposeAsync(this::throwIfFailed);
    Props nodeProps =
        getProps
            .handleAsync(this::orPartialRollback)
            .thenComposeAsync(x -> getProps)
            .thenComposeAsync(props -> addQueueThenStoreIds(props, qConfig))
            .handleAsync(this::orCompleteRollback)
            .thenComposeAsync(x -> getProps)
            .toCompletableFuture()
            .join();
    return mapToResponse(nodeProps);
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
                    .runtime(config.getRuntime())
                    .handler(config.getFunctionName())
                    .memorySize(config.getMemorySizeInMegabytes())
                    .timeout(config.getTimeoutInSeconds())
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
      result = result.whenCompleteAsync(this::throwRuntimeException);
    }
    return result;
  }

  private CreateNodeResponse mapToResponse(Props props) {
    return CreateNodeResponse.newBuilder()
        .setFunctionId(props.function.id)
        .setQueueId(props.queue.id)
        .build();
  }

  private FunctionCode getFunctionCode(FunctionConfig config) {
    return FunctionCode.builder()
        .s3Bucket(config.getCodeBucket())
        .s3Key(config.getCodeKey())
        .build();
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
        String.valueOf(config.getDelayInSeconds()),
        QueueAttributeName.MAXIMUM_MESSAGE_SIZE,
        String.valueOf(config.getMaximumMessageSizeInBytes()),
        QueueAttributeName.MESSAGE_RETENTION_PERIOD,
        String.valueOf(config.getMessageRetentionPeriodInSeconds()),
        QueueAttributeName.POLICY,
        "", // TODO
        QueueAttributeName.RECEIVE_MESSAGE_WAIT_TIME_SECONDS,
        String.valueOf(config.getReceiveMessageWaitTimeInSeconds()),
        QueueAttributeName.VISIBILITY_TIMEOUT,
        String.valueOf(config.getVisibilityTimeoutInSeconds()),
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
                    .batchSize(config.getBatchSize())),
        FailedEventSourceMappingException::new);
  }

  private CompletionStage<Void> putItem(String nodeId, String functionId, String queueId) {
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
