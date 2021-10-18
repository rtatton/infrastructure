package org.cirrus.infrastructure.handler;

import dagger.Module;
import dagger.Provides;
import java.util.Map;
import java.util.function.Supplier;
import javax.inject.Singleton;
import org.cirrus.infrastructure.util.Logger;
import org.cirrus.infrastructure.util.ResourceUtil;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

@Module
final class CreateQueueModule {

  // Use defaults if not specified.
  private static final Map<QueueAttributeName, String> ATTRIBUTES =
      Map.of(
          QueueAttributeName.POLICY, "", // TODO
          QueueAttributeName.VISIBILITY_TIMEOUT, "", // TODO Move to API
          QueueAttributeName.MESSAGE_RETENTION_PERIOD, "" // TODO Move to API
          );
  private static final String LOGGER_NAME = "CreateQueue";

  private CreateQueueModule() {
    // No-op
  }

  @Provides
  public static Supplier<CreateQueueRequest> provideCreateRequester() {
    return () ->
        CreateQueueRequest.builder()
            .queueName(ResourceUtil.createRandomId())
            .attributes(ATTRIBUTES)
            .build();
  }

  @Provides
  @Singleton
  public static Logger provideLogger() {
    return Logger.of(LOGGER_NAME);
  }
}