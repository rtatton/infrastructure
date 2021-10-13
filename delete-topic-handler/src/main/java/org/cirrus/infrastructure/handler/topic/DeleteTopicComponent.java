package org.cirrus.infrastructure.handler.topic;

import dagger.Component;
import javax.inject.Singleton;
import org.cirrus.infrastructure.handler.util.ClientModule;
import org.cirrus.infrastructure.handler.util.Logger;

@Singleton
@Component(modules = {ClientModule.class, DeleteTopicModule.class})
interface DeleteTopicComponent {

  DeleteTopicCommandFactory getDeleteTopicCommandFactory();

  Logger getLogger();
}
