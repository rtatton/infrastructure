package org.cirrus.infrastructure.task.function;

import dagger.Module;
import dagger.Provides;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.inject.Singleton;
import org.cirrus.infrastructure.task.util.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.CreateEventSourceMappingRequest;
import software.amazon.awssdk.services.lambda.model.CreateFunctionRequest;
import software.amazon.awssdk.services.lambda.model.DeleteFunctionRequest;
import software.amazon.awssdk.services.lambda.model.FunctionCode;
import software.amazon.awssdk.services.lambda.model.PackageType;

@Module
final class FunctionModule {

  private static final Region REGION = Region.US_EAST_2;
  private static final String ACCESS_KEY = "";
  private static final String SECRETE_KEY = "";
  private static final AwsCredentials CREDENTIALS =
      AwsBasicCredentials.create(ACCESS_KEY, SECRETE_KEY);
  private static final AwsCredentialsProvider CREDENTIALS_PROVIDER =
      StaticCredentialsProvider.create(CREDENTIALS);
  private static final String HANDLER_NAME = "handler";
  private static final String CODE_SIGNING_CONFIG_ARN = "";
  private static final int MEMORY_SIZE_IN_MB = 256;
  private static final PackageType PACKAGE_TYPE = PackageType.IMAGE;
  private static final int TIMEOUT_IN_SECONDS = 3;
  private static final String FUNCTION_ROLE = "";
  private static final String IMAGE_URI = "";
  private static final int BATCH_SIZE = 10;
  private static final int MAX_BATCHING_WINDOW_IN_SECONDS = 10;
  private static final FunctionCode CODE = FunctionCode.builder().imageUri(IMAGE_URI).build();
  private static final Logger logger = LoggerFactory.getLogger("FunctionLogger");

  private FunctionModule() {}

  @Provides
  @Singleton
  public static LambdaAsyncClient provideLambdaClient() {
    return LambdaAsyncClient.builder()
        .region(REGION)
        .credentialsProvider(CREDENTIALS_PROVIDER)
        .build();
  }

  @Provides
  public static Supplier<CreateFunctionRequest> provideCreateRequester() {
    return () ->
        CreateFunctionRequest.builder()
            .functionName(ResourceUtil.createRandomId())
            .packageType(PACKAGE_TYPE)
            .code(CODE)
            .handler(HANDLER_NAME)
            .codeSigningConfigArn(CODE_SIGNING_CONFIG_ARN)
            .memorySize(MEMORY_SIZE_IN_MB)
            .timeout(TIMEOUT_IN_SECONDS)
            .role(FUNCTION_ROLE)
            .publish(true)
            .build();
  }

  @Provides
  @Singleton
  public static Function<String, DeleteFunctionRequest> provideDeleteRequester() {
    return functionId -> DeleteFunctionRequest.builder().functionName(functionId).build();
  }

  @Provides
  @Singleton
  public static BiFunction<String, String, CreateEventSourceMappingRequest>
      provideEventSourceRequester() {
    return (functionId, queueId) ->
        CreateEventSourceMappingRequest.builder()
            .functionName(functionId)
            .eventSourceArn(queueId)
            .batchSize(BATCH_SIZE)
            .maximumBatchingWindowInSeconds(MAX_BATCHING_WINDOW_IN_SECONDS)
            .build();
  }

  @Provides
  @Singleton
  public static Consumer<Throwable> provideLogger() {
    return throwable -> logger.error(throwable.getLocalizedMessage());
  }
}
