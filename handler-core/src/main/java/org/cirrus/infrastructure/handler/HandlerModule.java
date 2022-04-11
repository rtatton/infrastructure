package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.blackbird.BlackbirdModule;
import dagger.Module;
import dagger.Provides;
import java.net.URI;
import java.time.Duration;
import javax.inject.Named;
import javax.inject.Singleton;
import org.cirrus.infrastructure.handler.model.NodeRecord;
import org.cirrus.infrastructure.handler.util.Resources;
import org.cirrus.infrastructure.util.Keys;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.CreateFunctionRequest;
import software.amazon.awssdk.services.lambda.model.PackageType;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.utils.builder.SdkBuilder;

// TODO Refactor to be more modular
// All clients are explicitly configured; recommended to improve Lambda performance.
@Module(includes = HandlerBindings.class)
final class HandlerModule {

  private static final String ENDPOINT_DOMAIN = "amazonaws.com";
  private static final String ENDPOINT_DELIMITER = ".";
  private static final String LAMBDA = "lambda";
  private static final String DYNAMODB = "dynamodb";
  private static final String S3 = "s3";
  private static final String SQS = "sqs";
  private static final String AWS_REGION = "AWS_REGION";

  private HandlerModule() {
    // No-op
  }

  @Provides
  @Singleton
  public static Region region() {
    // Recommended to specify explicitly to improve Lambda performance.
    return Region.of(System.getenv(AWS_REGION));
  }

  @Provides
  @Singleton
  public static AwsCredentialsProvider credentialsProvider() {
    // Recommended to improve Lambda performance.
    return EnvironmentVariableCredentialsProvider.create();
  }

  @Provides
  public static SdkAsyncHttpClient.Builder<?> httpClientBuilder() {
    // Recommended to improve Lambda performance.
    return AwsCrtAsyncHttpClient.builder();
  }

  @Provides
  @Singleton
  public static LambdaAsyncClient lambdaClient(
      Region region,
      AwsCredentialsProvider credentialsProvider,
      SdkAsyncHttpClient.Builder<?> clientBuilder) {
    return LambdaAsyncClient.builder()
        .region(region)
        .credentialsProvider(credentialsProvider)
        .httpClientBuilder(clientBuilder)
        .overrideConfiguration(SdkBuilder::build)
        .endpointOverride(endpoint(LAMBDA, region))
        .build();
  }

  @Provides
  public static CreateFunctionRequest.Builder runtimeBuilder() {
    return CreateFunctionRequest.builder()
        .functionName(Resources.createRandomId())
        .role(System.getenv(Keys.NODE_ROLE))
        .runtime(System.getenv(Keys.NODE_RUNTIME))
        .handler(System.getenv(Keys.NODE_HANDLER))
        .packageType(PackageType.ZIP)
        .code(
            builder ->
                builder
                    .s3Bucket(System.getenv(Keys.NODE_BUCKET))
                    .s3Key(System.getenv(Keys.NODE_KEY)))
        .publish(true);
  }

  @Provides
  @Singleton
  public static SqsAsyncClient sqsClient(
      Region region,
      AwsCredentialsProvider credentialsProvider,
      SdkAsyncHttpClient.Builder<?> clientBuilder) {
    return SqsAsyncClient.builder()
        .region(region)
        .credentialsProvider(credentialsProvider)
        .httpClientBuilder(clientBuilder)
        .overrideConfiguration(SdkBuilder::build)
        .endpointOverride(endpoint(SQS, region))
        .build();
  }

  @Provides
  @Singleton
  public static DynamoDbAsyncClient dynamoDbClient(
      Region region,
      AwsCredentialsProvider credentialsProvider,
      SdkAsyncHttpClient.Builder<?> clientBuilder) {
    return DynamoDbAsyncClient.builder()
        .region(region)
        .credentialsProvider(credentialsProvider)
        .httpClientBuilder(clientBuilder)
        .overrideConfiguration(SdkBuilder::build)
        .endpointOverride(endpoint(DYNAMODB, region))
        .build();
  }

  @Provides
  @Singleton
  public static DynamoDbEnhancedAsyncClient enhancedDynamoDbClient(DynamoDbAsyncClient dynamoDb) {
    return DynamoDbEnhancedAsyncClient.builder().dynamoDbClient(dynamoDb).build();
  }

  @Provides
  @Singleton
  public static DynamoDbAsyncTable<NodeRecord> nodeTable(DynamoDbEnhancedAsyncClient dynamoDb) {
    return dynamoDb.table(Keys.NODE_TABLE_NAME, TableSchema.fromImmutableClass(NodeRecord.class));
  }

  @Provides
  @Singleton
  public static S3Presigner signer(Region region, AwsCredentialsProvider credentialsProvider) {
    return S3Presigner.builder()
        .region(region)
        .credentialsProvider(credentialsProvider)
        .endpointOverride(endpoint(S3, region))
        .dualstackEnabled(false)
        .fipsEnabled(false)
        .serviceConfiguration(
            S3Configuration.builder()
                .pathStyleAccessEnabled(false)
                .accelerateModeEnabled(false)
                .useArnRegionEnabled(true)
                .multiRegionEnabled(true)
                .checksumValidationEnabled(true)
                .chunkedEncodingEnabled(false)
                .build())
        .build();
  }

  @Provides
  @Singleton
  @Named("uploadBucket")
  public static String uploadBucket() {
    return System.getenv(Keys.CODE_UPLOAD_BUCKET);
  }

  @Provides
  @Singleton
  @Named("uploadContentType")
  public static String codeUploadContentType() {
    return "application/zip";
  }

  @Provides
  @Singleton
  @Named("uploadSignatureTtl")
  public static Duration codeUploadSignatureTtl() {
    return Duration.ofHours(1);
  }

  @Provides
  @Singleton
  public static ObjectMapper objectMapper() {
    return new ObjectMapper()
        .registerModule(new Jdk8Module())
        .registerModule(new BlackbirdModule());
  }

  private static URI endpoint(String service, Region region) {
    return URI.create(String.join(ENDPOINT_DELIMITER, service, region.toString(), ENDPOINT_DOMAIN));
  }
}
