package org.cirrus.infrastructure.handler;

import dagger.Module;
import dagger.Provides;
import java.util.function.Function;
import javax.inject.Singleton;
import org.cirrus.infrastructure.util.Logger;
import software.amazon.awssdk.services.sqs.model.DeleteQueueRequest;

@Module
final class DeleteQueueModule {

  private static final String LOGGER_NAME = "DeleteQueue";

  private DeleteQueueModule() {
    // No-op
  }

  @Provides
  @Singleton
  public static Function<String, DeleteQueueRequest> provideDeleteRequester() {
    return queueId -> DeleteQueueRequest.builder().queueUrl(queueId).build();
  }

  @Provides
  @Singleton
  public static Logger provideLogger() {
    return Logger.of(LOGGER_NAME);
  }
}
