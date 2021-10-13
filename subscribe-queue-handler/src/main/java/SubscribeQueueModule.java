import dagger.Module;
import dagger.Provides;
import java.util.function.BiFunction;
import javax.inject.Singleton;
import org.cirrus.infrastructure.handler.util.Logger;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;

@Module
final class SubscribeQueueModule {

  private static final String SUBSCRIPTION_PROTOCOL = "sqs";

  private SubscribeQueueModule() {}

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
  public static Logger provideLogger() {
    return Logger.of("SubscribeQueue");
  }
}
