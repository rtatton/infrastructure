package org.cirrus.infrastructure.task.topic;

import dagger.Component;
import java.util.function.Consumer;
import javax.inject.Singleton;

@Singleton
@Component(modules = {TopicModule.class})
interface TopicComponent {

  CreateTopic newCreateTopic();

  DeleteTopicFactory newDeleteTopicFactory();

  SubscribeQueueFactory newSubscribeQueueFactory();

  Consumer<Throwable> newLogger();
}
