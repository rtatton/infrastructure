package org.cirrus.infrastructure.task.topic;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;

@AssistedFactory
interface DeleteTopicFactory {

  DeleteTopic create(@Assisted String topicId);
}
