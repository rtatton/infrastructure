package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.blackbird.BlackbirdModule;
import dagger.Module;
import dagger.Provides;
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
import software.amazon.awssdk.services.lambda.model.FunctionCode;
import software.amazon.awssdk.services.lambda.model.PackageType;
import software.amazon.awssdk.services.lambda.model.Runtime;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

// TODO Be more explicit https://youtu.be/ddg1u5HLwg8?t=999
@Module(includes = HandlerBindings.class)
final class HandlerModule {

  private HandlerModule() {
    // No-op
  }

  @Provides
  @Singleton
  public static Region region() {
    // Recommended to specify explicitly to improve Lambda performance.
    return Region.of(System.getenv(Keys.AWS_REGION));
  }

  @Provides
  @Singleton
  public static AwsCredentialsProvider credentialsProvider() {
    // Recommended to improve Lambda performance.
    return EnvironmentVariableCredentialsProvider.create();
  }

  @Provides
  public static SdkAsyncHttpClient.Builder<?> httpClientBuilder() {
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
        .build();
  }

  @Provides
  public static CreateFunctionRequest.Builder runtimeBuilder() {
    return CreateFunctionRequest.builder()
        .functionName(Resources.createRandomId())
        .role(System.getenv(Keys.NODE_FUNCTION_ROLE))
        .runtime(Runtime.PYTHON3_8) // TODO - environment variable
        .handler("") // TODO - environment variable
        .packageType(PackageType.ZIP)
        .code(FunctionCode.builder().build()) // TODO - environment variable
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
    return S3Presigner.builder().region(region).credentialsProvider(credentialsProvider).build();
  }

  @Provides
  @Singleton
  @Named("uploadBucket")
  public static String codeBucketName() {
    return Keys.CODE_BUCKET_NAME;
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
}
