package org.cirrus.infrastructure.task.queue;

import dagger.Module;
import dagger.Provides;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.inject.Singleton;
import org.cirrus.infrastructure.task.util.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.DeleteQueueRequest;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

@Module
final class QueueModule {

  private static final Region REGION = Region.US_EAST_2;
  private static final String ACCESS_KEY = "";
  private static final String SECRETE_KEY = "";
  private static final AwsCredentials CREDENTIALS =
      AwsBasicCredentials.create(ACCESS_KEY, SECRETE_KEY);
  private static final AwsCredentialsProvider CREDENTIALS_PROVIDER =
      StaticCredentialsProvider.create(CREDENTIALS);
  private static final Map<QueueAttributeName, String> ATTRIBUTES = Map.of();
  private static final Logger logger = LoggerFactory.getLogger("QueueLogger");

  private QueueModule() {}

  @Provides
  @Singleton
  public static SqsAsyncClient provideSqsClient() {
    return SqsAsyncClient.builder()
        .region(REGION)
        .credentialsProvider(CREDENTIALS_PROVIDER)
        .build();
  }

  @Provides
  public static Supplier<CreateQueueRequest> provideCreateRequester() {
    return () ->
        CreateQueueRequest.builder()
            .queueName(Resources.createRandomId())
            .attributes(ATTRIBUTES)
            .build();
  }

  @Provides
  @Singleton
  public static Function<String, DeleteQueueRequest> provideDeleteRequester() {
    return queueId -> DeleteQueueRequest.builder().queueUrl(queueId).build();
  }

  @Provides
  @Singleton
  public static Consumer<Throwable> provideLogger() {
    return throwable -> logger.error(throwable.getLocalizedMessage());
  }
}
