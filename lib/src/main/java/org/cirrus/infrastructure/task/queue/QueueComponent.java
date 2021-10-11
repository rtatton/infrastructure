package org.cirrus.infrastructure.task.queue;

import dagger.Component;
import java.util.function.Consumer;
import javax.inject.Singleton;

@Singleton
@Component(modules = {QueueModule.class})
interface QueueComponent {

  CreateQueue newCreateQueue();

  DeleteQueueFactory newDeleteQueueFactory();

  Consumer<Throwable> newLogger();
}
