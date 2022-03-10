package org.cirrus.infrastructure.handler.service;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.inject.Named;
import org.cirrus.infrastructure.handler.exception.FailedCodePublicationException;
import org.cirrus.infrastructure.handler.exception.FailedEventSourceMappingException;
import org.cirrus.infrastructure.handler.exception.FailedResourceCreationException;
import org.cirrus.infrastructure.handler.exception.FailedResourceDeletionException;
import org.cirrus.infrastructure.handler.model.FunctionConfig;
import org.cirrus.infrastructure.handler.model.QueueConfig;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.CreateEventSourceMappingResponse;
import software.amazon.awssdk.services.lambda.model.CreateFunctionRequest;
import software.amazon.awssdk.services.lambda.model.CreateFunctionResponse;
import software.amazon.awssdk.services.lambda.model.LayerVersionContentInput;
import software.amazon.awssdk.services.lambda.model.PublishLayerVersionResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

public class LambdaFunctionService implements FunctionService {

  private final LambdaAsyncClient lambdaClient;
  private final S3Presigner signer;
  private final ServiceHelper helper;
  private final CreateFunctionRequest.Builder runtimeBuilder;
  private final String uploadBucket;
  private final String contentType;
  private final Duration signatureTtl;

  @Inject
  public LambdaFunctionService(
      LambdaAsyncClient lambdaClient,
      S3Presigner signer,
      ServiceHelper helper,
      CreateFunctionRequest.Builder runtimeBuilder,
      @Named("uploadBucket") String uploadBucket,
      @Named("uploadContentType") String contentType,
      @Named("uploadSignatureTtl") Duration signatureTtl) {
    this.lambdaClient = lambdaClient;
    this.signer = signer;
    this.helper = helper;
    this.runtimeBuilder = runtimeBuilder;
    this.uploadBucket = uploadBucket;
    this.contentType = contentType;
    this.signatureTtl = signatureTtl;
  }

  @Override
  public CompletableFuture<String> getUploadUrl(String codeId) {
    return CompletableFuture.completedFuture(signedUrl(codeId));
  }

  @Override
  public CompletableFuture<String> publishCode(String codeId, String runtime) {
    return helper.getOrThrow(
        lambdaClient.publishLayerVersion(
            builder ->
                builder
                    .layerName(codeId)
                    .compatibleRuntimesWithStrings(runtime)
                    .content(layerContent(codeId))),
        PublishLayerVersionResponse::layerArn,
        FailedCodePublicationException::new);
  }

  @Override
  public CompletableFuture<String> createFunction(FunctionConfig config) {
    return helper.getOrThrow(
        lambdaClient.createFunction(createFunctionRequest(config)),
        CreateFunctionResponse::functionArn,
        FailedResourceCreationException::new);
  }

  @Override
  public CompletableFuture<Void> deleteFunction(String functionId) {
    return helper.getOrThrow(
        lambdaClient.deleteFunction(builder -> builder.functionName(functionId)),
        FailedResourceDeletionException::new);
  }

  @Override
  public CompletableFuture<String> attachQueue(
      String functionId, String queueId, QueueConfig config) {
    return helper.getOrThrow(
        lambdaClient.createEventSourceMapping(
            builder ->
                builder
                    .functionName(functionId)
                    .eventSourceArn(queueId)
                    .batchSize(config.batchSize())),
        CreateEventSourceMappingResponse::eventSourceArn,
        FailedEventSourceMappingException::new);
  }

  private CreateFunctionRequest createFunctionRequest(FunctionConfig config) {
    return runtimeBuilder
        .applyMutation(
            builder ->
                builder
                    .memorySize(config.memorySizeMegabytes())
                    .layers(config.codeId())
                    .timeout(config.timeoutSeconds())
                    .environment(envBuilder -> envBuilder.variables(config.environment())))
        .build();
  }

  private String signedUrl(String codeId) {
    return signer.presignPutObject(request(codeId)).url().toString();
  }

  private PutObjectPresignRequest request(String codeId) {
    return PutObjectPresignRequest.builder()
        .putObjectRequest(
            builder -> builder.contentType(contentType).bucket(uploadBucket).key(codeId).build())
        .signatureDuration(signatureTtl)
        .build();
  }

  private LayerVersionContentInput layerContent(String codeId) {
    return LayerVersionContentInput.builder().s3Bucket(uploadBucket).s3Key(codeId).build();
  }
}
