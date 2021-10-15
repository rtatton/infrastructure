package org.cirrus.infrastructure.handler.topic;

import dagger.Module;
import dagger.Provides;
import java.util.Map;
import java.util.function.BiFunction;
import javax.inject.Singleton;
import org.cirrus.infrastructure.util.Logger;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;

@Module
final class SubscribeQueueModule {

  private static final String SUBSCRIPTION_PROTOCOL = "sqs";
  private static final String LOGGER_NAME = "SubscribeQueue";
  // TODO RawMessageDelivery - Is this possible, given the Message class?
  // TODO FilterPolicy - possible to allow both pub/sub and point-to-point through subscription?
  private static final Map<String, String> ATTRIBUTES = Map.of();

  private SubscribeQueueModule() {
    // No-op
  }

  @Provides
  @Singleton
  public static BiFunction<String, String, SubscribeRequest> provideSubscribeRequester() {
    return (topicId, queueId) ->
        SubscribeRequest.builder()
            .topicArn(topicId)
            .endpoint(queueId)
            .attributes(ATTRIBUTES)
            .protocol(SUBSCRIPTION_PROTOCOL)
            .build();
  }

  @Provides
  @Singleton
  public static Logger provideLogger() {
    return Logger.of(LOGGER_NAME);
  }
}
