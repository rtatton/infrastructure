package org.cirrus.infrastructure.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import org.cirrus.infrastructure.handler.exception.FailedEventSourceMappingException;
import org.cirrus.infrastructure.handler.exception.FailedResourceCreationException;
import org.cirrus.infrastructure.handler.exception.FailedResourceDeletionException;
import org.cirrus.infrastructure.handler.exception.FailedStorageWriteException;
import org.cirrus.infrastructure.util.Logger;
import org.cirrus.infrastructure.util.Mapper;
import org.cirrus.infrastructure.util.ResourceUtil;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.CreateFunctionResponse;
import software.amazon.awssdk.services.lambda.model.FunctionCode;
import software.amazon.awssdk.services.lambda.model.PackageType;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

public class CreateNodeHandler implements RequestHandler<APIGatewayV2HTTPEvent, String> {

  private static final LambdaAsyncClient lambda = LambdaAsyncClient.create();
  private static final SqsAsyncClient sqs = SqsAsyncClient.create();
  private static final DynamoDbEnhancedAsyncClient dynamoDb =
      DynamoDbEnhancedAsyncClient.builder().dynamoDbClient(DynamoDbAsyncClient.create()).build();
  private static final DynamoDbAsyncTable<NodeRecord> nodeRegistry =
      dynamoDb.table("NodeRegistry", TableSchema.fromImmutableClass(NodeRecord.class));
  private static final Mapper mapper = Mapper.create();
  private static final Logger logger = Logger.create();
  private static final String TRUE = "true";

  @Override
  public String handleRequest(APIGatewayV2HTTPEvent event, Context context) {
    CreateNodeRequest request = mapToInput(event.getBody());
    CreateNodeResponse response = createNode(request);
    return mapToOutput(response);
  }

  private CreateNodeRequest mapToInput(String data) {
    return mapper.read(data, CreateNodeRequest.class, logger);
  }

  private CreateNodeResponse createNode(CreateNodeRequest request) {
    FunctionConfig functionConfig = request.getFunctionConfig();
    QueueConfig queueConfig = request.getQueueConfig();
    CompletionStage<Resource> createFunction = createFunction(functionConfig);
    CompletionStage<Resource> createQueue = createQueue(queueConfig);
    CompletionStage<NodeProps> getProps = getProps(request.getName(), createFunction, createQueue);
    getProps = orPartialRollback(getProps);
    getProps = addQueueThenStoreIds(getProps, queueConfig);
    NodeProps props = orCompleteRollback(getProps).toCompletableFuture().join();
    return mapToResponse(props);
  }

  private CreateNodeResponse mapToResponse(NodeProps props) {
    return CreateNodeResponse.newBuilder()
        .setFunctionId(props.function.id)
        .setQueueId(props.queue.id)
        .build();
  }

  private String mapToOutput(CreateNodeResponse response) {
    return mapper.write(response, logger);
  }

  private CompletionStage<NodeProps> getProps(
      String nodeId,
      CompletionStage<Resource> createFunction,
      CompletionStage<Resource> createQueue) {
    return createFunction
        .thenCombineAsync(createQueue, (func, queue) -> new NodeProps(nodeId, func, queue))
        .thenComposeAsync(this::orElseThrow);
  }

  private CompletionStage<Resource> createFunction(FunctionConfig config) {
    CompletionStage<CreateFunctionResponse> response =
        lambda.createFunction(
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
        sqs.createQueue(builder -> builder.queueName(queueId).attributes(props));
    return createResource(response, CreateQueueResponse::queueUrl);
  }

  private CompletionStage<NodeProps> orPartialRollback(CompletionStage<NodeProps> getProps) {
    return getProps
        .handleAsync(
            (props, throwable) -> {
              CompletionStage<?> result;
              if (throwable == null) {
                result = getProps;
              } else {
                if (props.function.id == null) {
                  result = deleteQueue(props.queue.id);
                } else {
                  result = deleteFunction(props.function.id);
                }
                result =
                    result.whenCompleteAsync(
                        (x, t) -> {
                          throw (RuntimeException) throwable;
                        });
              }
              return result;
            })
        .thenComposeAsync(x -> getProps);
  }

  private CompletionStage<NodeProps> addQueueThenStoreIds(
      CompletionStage<NodeProps> getNodeProps, QueueConfig config) {
    return getNodeProps
        .thenComposeAsync(
            props ->
                addQueue(props, config)
                    .thenComposeAsync(
                        x -> putItem(props.nodeId, props.function.id, props.queue.id)))
        .thenComposeAsync(x -> getNodeProps);
  }

  private CompletionStage<NodeProps> orCompleteRollback(CompletionStage<NodeProps> getProps) {
    return getProps
        .handleAsync(
            (props, throwable) -> {
              CompletionStage<?> result;
              if (throwable == null) {
                result = getProps;
              } else {
                result =
                    deleteQueue(props.queue.id)
                        .whenCompleteAsync((x, t) -> deleteFunction(props.function.id))
                        .whenCompleteAsync(
                            (x, t) -> {
                              throw (RuntimeException) throwable;
                            });
              }
              return result;
            })
        .thenComposeAsync(x -> getProps);
  }

  private CompletionStage<NodeProps> orElseThrow(NodeProps props) {
    if (props.function.id != null && props.queue.id != null) {
      return CompletableFuture.completedFuture(props);
    } else if (props.function.exception != null) {
      throw props.function.exception;
    } else {
      throw props.queue.exception;
    }
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

  private CompletionStage<?> deleteQueue(String queueId) {
    return wrapThrowable(
        sqs.deleteQueue(builder -> builder.queueUrl(queueId)),
        FailedResourceDeletionException::new);
  }

  private CompletionStage<?> deleteFunction(String functionId) {
    return wrapThrowable(
        lambda.deleteFunction(builder -> builder.functionName(functionId)),
        FailedResourceDeletionException::new);
  }

  private CompletionStage<?> addQueue(NodeProps props, QueueConfig config) {
    return wrapThrowable(
        lambda.createEventSourceMapping(
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

  private static class NodeProps {

    private final String nodeId;
    private final Resource function;
    private final Resource queue;

    private NodeProps(String nodeId, Resource function, Resource queue) {
      this.nodeId = nodeId;
      this.function = function;
      this.queue = queue;
    }
  }
}
