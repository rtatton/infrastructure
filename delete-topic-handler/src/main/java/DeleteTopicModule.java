import dagger.Module;
import dagger.Provides;
import java.util.function.Function;
import javax.inject.Singleton;
import org.cirrus.infrastructure.handler.util.Logger;
import software.amazon.awssdk.services.sns.model.DeleteTopicRequest;

@Module
final class DeleteTopicModule {

  private DeleteTopicModule() {}

  @Provides
  @Singleton
  public static Function<String, DeleteTopicRequest> provideDeleteRequester() {
    return topicId -> DeleteTopicRequest.builder().topicArn(topicId).build();
  }

  @Provides
  @Singleton
  public static Logger provideLogger() {
    return Logger.of("DeleteTopic");
  }
}
