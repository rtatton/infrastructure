package org.cirrus.infrastructure.task.topic;

import dagger.Component;
import java.util.function.Consumer;
import javax.inject.Singleton;

@Singleton
@Component(modules = {TopicModule.class})
interface TopicComponent {

  CreateTopic getCreateTopic();

  DeleteTopicFactory getDeleteTopicFactory();

  SubscribeQueueFactory getSubscribeQueueFactory();

  Consumer<Throwable> getLogger();
}
