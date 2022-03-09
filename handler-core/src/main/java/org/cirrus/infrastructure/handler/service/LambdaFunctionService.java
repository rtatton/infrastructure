package org.cirrus.infrastructure.handler.service;

import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.inject.Named;
import org.cirrus.infrastructure.handler.exception.FailedCodePublicationException;
import org.cirrus.infrastructure.handler.exception.FailedEventSourceMappingException;
import org.cirrus.infrastructure.handler.exception.FailedResourceDeletionException;
import org.cirrus.infrastructure.handler.model.FunctionConfig;
import org.cirrus.infrastructure.handler.model.QueueConfig;
import org.cirrus.infrastructure.handler.model.Resource;
import org.cirrus.infrastructure.handler.util.Resources;
import org.cirrus.infrastructure.util.Keys;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.CreateEventSourceMappingResponse;
import software.amazon.awssdk.services.lambda.model.CreateFunctionResponse;
import software.amazon.awssdk.services.lambda.model.FunctionCode;
import software.amazon.awssdk.services.lambda.model.LayerVersionContentInput;
import software.amazon.awssdk.services.lambda.model.PackageType;
import software.amazon.awssdk.services.lambda.model.PublishLayerVersionResponse;

public class LambdaFunctionService implements FunctionService {

  private final LambdaAsyncClient lambdaClient;
  private final ServiceHelper helper;
  private final String uploadBucket;

  @Inject
  public LambdaFunctionService(
      LambdaAsyncClient lambdaClient,
      ServiceHelper helper,
      @Named("uploadBucket") String uploadBucket) {
    this.lambdaClient = lambdaClient;
    this.helper = helper;
    this.uploadBucket = uploadBucket;
  }

  private static String role() {
    return System.getenv(Keys.NODE_FUNCTION_ROLE);
  }

  private static FunctionCode functionCode(FunctionConfig config) {
    return FunctionCode.builder().s3Bucket(config.codeBucket()).s3Key(config.codeKey()).build();
  }

  @Override
  public CompletionStage<String> publishCode(String codeId, String runtime) {
    return helper.wrapThrowable(
        lambdaClient
            .publishLayerVersion(
                builder ->
                    builder
                        .layerName(codeId)
                        .compatibleRuntimesWithStrings(runtime)
                        .content(layerContent(codeId)))
            .thenApplyAsync(PublishLayerVersionResponse::layerArn),
        FailedCodePublicationException::new);
  }

  @Override
  public CompletionStage<Resource> create(FunctionConfig config) {
    CompletionStage<CreateFunctionResponse> response =
        lambdaClient.createFunction(
            builder ->
                builder
                    .functionName(Resources.createRandomId())
                    .packageType(PackageType.ZIP)
                    .code(functionCode(config))
                    .runtime(config.runtime()) // TODO This should by Python for ACA-Py
                    .handler(config.handlerName())
                    .memorySize(config.memorySizeMegabytes())
                    .layers() // TODO This is the controller
                    .timeout(config.timeoutSeconds())
                    .role(role())
                    .publish(true));
    return helper.createResource(response, CreateFunctionResponse::functionArn);
  }

  @Override
  public CompletionStage<Void> delete(String functionId) {
    return helper
        .wrapThrowable(
            lambdaClient.deleteFunction(builder -> builder.functionName(functionId)),
            FailedResourceDeletionException::new)
        .thenApplyAsync(x -> null);
  }

  @Override
  public CompletionStage<String> attachQueue(
      String functionId, String queueId, QueueConfig config) {
    return helper
        .wrapThrowable(
            lambdaClient.createEventSourceMapping(
                builder ->
                    builder
                        .functionName(functionId)
                        .eventSourceArn(queueId)
                        .batchSize(config.batchSize())),
            FailedEventSourceMappingException::new)
        .thenApplyAsync(CreateEventSourceMappingResponse::eventSourceArn);
  }

  private LayerVersionContentInput layerContent(String codeKey) {
    return LayerVersionContentInput.builder().s3Bucket(uploadBucket).s3Key(codeKey).build();
  }
}
