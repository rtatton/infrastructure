package org.cirrus.infrastructure.handler.queue;

import dagger.Component;
import javax.inject.Singleton;
import org.cirrus.infrastructure.handler.util.ClientModule;
import org.cirrus.infrastructure.handler.util.Logger;

@Singleton
@Component(modules = {ClientModule.class, CreateQueueModule.class})
interface CreateQueueComponent {

  CreateQueueCommand getCreateQueueCommand();

  Logger getLogger();
}
