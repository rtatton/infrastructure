package org.cirrus.infrastructure.task.queue;

import dagger.Component;
import java.util.function.Consumer;
import javax.inject.Singleton;

@Singleton
@Component(modules = {QueueModule.class})
interface QueueComponent {

  CreateQueueCommand getCreateQueueCommand();

  DeleteQueueCommandFactory getDeleteQueueCommandFactory();

  Consumer<Throwable> getLogger();
}
