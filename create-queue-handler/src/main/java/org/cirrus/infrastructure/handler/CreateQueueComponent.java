package org.cirrus.infrastructure.handler;

import dagger.Component;
import javax.inject.Singleton;
import org.cirrus.infrastructure.util.ClientModule;
import org.cirrus.infrastructure.util.Logger;

@Singleton
@Component(modules = {ClientModule.class, CreateQueueModule.class})
interface CreateQueueComponent {

  CreateQueueCommand getCreateQueueCommand();

  Logger getLogger();
}
