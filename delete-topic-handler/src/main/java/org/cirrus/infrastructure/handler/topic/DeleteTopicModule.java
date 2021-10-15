package org.cirrus.infrastructure.handler.topic;

import dagger.Module;
import dagger.Provides;
import java.util.function.Function;
import javax.inject.Singleton;
import org.cirrus.infrastructure.util.Logger;
import software.amazon.awssdk.services.sns.model.DeleteTopicRequest;

@Module
final class DeleteTopicModule {

  private static final String LOGGER_NAME = "DeleteTopic";

  private DeleteTopicModule() {
    // No-op
  }

  @Provides
  @Singleton
  public static Function<String, DeleteTopicRequest> provideDeleteRequester() {
    return topicId -> DeleteTopicRequest.builder().topicArn(topicId).build();
  }

  @Provides
  @Singleton
  public static Logger provideLogger() {
    return Logger.of(LOGGER_NAME);
  }
}
