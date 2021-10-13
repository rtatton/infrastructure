package org.cirrus.infrastructure.handler.topic;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;

@AssistedFactory
interface DeleteTopicCommandFactory {

  DeleteTopicCommand create(@Assisted String topicId);
}