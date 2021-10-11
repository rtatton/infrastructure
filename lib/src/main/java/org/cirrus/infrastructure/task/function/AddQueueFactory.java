package org.cirrus.infrastructure.task.function;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;

@AssistedFactory
interface AddQueueFactory {

  AddQueue create(@Assisted("functionId") String functionId, @Assisted("queueId") String queueId);
}
