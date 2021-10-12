package org.cirrus.infrastructure.task.queue;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;

@AssistedFactory
interface DeleteQueueCommandFactory {

  DeleteQueueCommand create(@Assisted String queueId);
}
