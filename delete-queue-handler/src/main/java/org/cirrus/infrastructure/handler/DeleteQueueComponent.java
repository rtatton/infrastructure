package org.cirrus.infrastructure.handler;

import dagger.Component;
import javax.inject.Singleton;
import org.cirrus.infrastructure.util.ClientModule;
import org.cirrus.infrastructure.util.Logger;

@Singleton
@Component(modules = {ClientModule.class, DeleteQueueModule.class})
interface DeleteQueueComponent {

  DeleteQueueCommandFactory getDeleteQueueCommandFactory();

  Logger getLogger();
}