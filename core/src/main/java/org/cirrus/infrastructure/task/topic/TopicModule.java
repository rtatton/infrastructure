package org.cirrus.infrastructure.task.topic;

import dagger.Module;
import dagger.Provides;
import java.util.Map;
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
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.DeleteTopicRequest;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;

@Module
final class TopicModule {

  private static final Region REGION = Region.US_EAST_2;
  private static final String ACCESS_KEY = "";
  private static final String SECRETE_KEY = "";
  private static final AwsCredentials CREDENTIALS =
      AwsBasicCredentials.create(ACCESS_KEY, SECRETE_KEY);
  private static final AwsCredentialsProvider CREDENTIALS_PROVIDER =
      StaticCredentialsProvider.create(CREDENTIALS);
  private static final Map<String, String> ATTRIBUTES = Map.of();
  private static final String SUBSCRIPTION_PROTOCOL = "sqs";
  private static final Logger logger = LoggerFactory.getLogger("TopicLogger");

  private TopicModule() {}

  @Provides
  @Singleton
  public static SnsAsyncClient provideSnsClient() {
    return SnsAsyncClient.builder()
        .region(REGION)
        .credentialsProvider(CREDENTIALS_PROVIDER)
        .build();
  }

  @Provides
  public static Supplier<CreateTopicRequest> provideCreateRequester() {
    return () ->
        CreateTopicRequest.builder()
            .name(ResourceUtil.createRandomId())
            .attributes(ATTRIBUTES)
            .build();
  }

  @Provides
  @Singleton
  public static Function<String, DeleteTopicRequest> provideDeleteRequester() {
    return topicId -> DeleteTopicRequest.builder().topicArn(topicId).build();
  }

  @Provides
  @Singleton
  public static BiFunction<String, String, SubscribeRequest> provideSubscribeRequester() {
    return (topicId, queueId) ->
        SubscribeRequest.builder()
            .topicArn(topicId)
            .endpoint(queueId)
            .protocol(SUBSCRIPTION_PROTOCOL)
            .build();
  }

  @Provides
  @Singleton
  public static Consumer<Throwable> provideLogger() {
    return throwable -> logger.error(throwable.getLocalizedMessage());
  }
}
