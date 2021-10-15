package org.cirrus.infrastructure.handler.topic;

import dagger.Module;
import dagger.Provides;
import java.util.Map;
import java.util.function.Supplier;
import javax.inject.Singleton;
import org.cirrus.infrastructure.util.Logger;
import org.cirrus.infrastructure.util.ResourceUtil;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;

@Module
final class CreateTopicModule {

  private static final Map<String, String> ATTRIBUTES = Map.of("Policy", ""); // TODO
  private static final String LOGGER_NAME = "CreateTopic";

  private CreateTopicModule() {
    // No-op
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
  public static Logger provideLogger() {
    return Logger.of(LOGGER_NAME);
  }
}
