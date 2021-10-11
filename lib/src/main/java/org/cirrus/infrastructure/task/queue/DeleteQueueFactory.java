package org.cirrus.infrastructure.task.queue;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;

@AssistedFactory
interface DeleteQueueFactory {

  DeleteQueue create(@Assisted String queueId);
}
