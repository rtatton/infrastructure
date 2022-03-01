package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.cirrus.infrastructure.handler.model.NodeRecord;
import org.cirrus.infrastructure.util.Keys;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Module(includes = HandlerBindings.class)
final class HandlerModule {

  // Recommended to specify explicitly to improve Lambda performance.
  private static final Region REGION = Region.US_EAST_2;
  // Recommended to improve Lambda performance.
  private static final AwsCredentialsProvider CREDENTIALS_PROVIDER =
      EnvironmentVariableCredentialsProvider.create();

  private HandlerModule() {
    // No-op
  }

  @Provides
  @Singleton
  public static LambdaAsyncClient lambdaClient() {
    return LambdaAsyncClient.builder()
        .region(REGION)
        .credentialsProvider(CREDENTIALS_PROVIDER)
        .httpClientBuilder(AwsCrtAsyncHttpClient.builder())
        .build();
  }

  @Provides
  @Singleton
  public static SqsAsyncClient sqsClient() {
    return SqsAsyncClient.builder()
        .region(REGION)
        .credentialsProvider(CREDENTIALS_PROVIDER)
        .httpClientBuilder(AwsCrtAsyncHttpClient.builder())
        .build();
  }

  @Provides
  @Singleton
  public static DynamoDbAsyncClient dynamoDbClient() {
    return DynamoDbAsyncClient.builder()
        .region(REGION)
        .credentialsProvider(CREDENTIALS_PROVIDER)
        .httpClientBuilder(AwsCrtAsyncHttpClient.builder())
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
  public static ObjectMapper objectMapper() {
    return new ObjectMapper().registerModule(new Jdk8Module());
  }
}
