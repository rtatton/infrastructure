package org.cirrus.infrastructure.task.topic;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;

@AssistedFactory
interface SubscribeQueueFactory {

  SubscribeQueue create(@Assisted("topicId") String topicId, @Assisted("queueId") String queueId);
}
