import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;

@AssistedFactory
interface SubscribeQueueCommandFactory {

  SubscribeQueueCommand create(
      @Assisted("topicId") String topicId, @Assisted("queueId") String queueId);
}
