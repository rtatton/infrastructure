package org.cirrus.infrastructure.handler.queue;

import dagger.Module;
import dagger.Provides;
import java.util.Map;
import java.util.function.Supplier;
import javax.inject.Singleton;
import org.cirrus.infrastructure.handler.util.Logger;
import org.cirrus.infrastructure.handler.util.ResourceUtil;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

@Module
final class CreateQueueModule {

  private static final Map<QueueAttributeName, String> ATTRIBUTES = Map.of();

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
    return Logger.of("CreateQueue");
  }
}
