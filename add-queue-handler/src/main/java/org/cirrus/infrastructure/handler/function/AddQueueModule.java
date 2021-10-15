package org.cirrus.infrastructure.handler.function;

import dagger.Module;
import dagger.Provides;
import java.util.function.BiFunction;
import javax.inject.Singleton;
import org.cirrus.infrastructure.util.Logger;
import software.amazon.awssdk.services.lambda.model.CreateEventSourceMappingRequest;

@Module
final class AddQueueModule {

  private static final int BATCH_SIZE = 10; // TODO Move to API
  private static final int MAX_BATCHING_WINDOW_IN_SECONDS = 10; // TODO Move to API
  private static final String LOGGER_NAME = "AddQueue";

  private AddQueueModule() {
    // No-op
  }

  @Provides
  @Singleton
  public static BiFunction<String, String, CreateEventSourceMappingRequest>
      provideEventSourceRequester() {
    return (functionId, queueId) ->
        CreateEventSourceMappingRequest.builder()
            .functionName(functionId)
            .eventSourceArn(queueId)
            .batchSize(BATCH_SIZE)
            .maximumBatchingWindowInSeconds(MAX_BATCHING_WINDOW_IN_SECONDS)
            .build();
  }

  @Provides
  @Singleton
  public static Logger provideLogger() {
    return Logger.of(LOGGER_NAME);
  }
}
