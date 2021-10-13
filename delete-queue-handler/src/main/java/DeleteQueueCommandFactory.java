import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;

@AssistedFactory
interface DeleteQueueCommandFactory {

  DeleteQueueCommand create(@Assisted String queueId);
}
