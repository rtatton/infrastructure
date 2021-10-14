package org.cirrus.infrastructure.handler.topic;

import dagger.Component;
import javax.inject.Singleton;
import org.cirrus.infrastructure.util.ClientModule;
import org.cirrus.infrastructure.util.Logger;

@Singleton
@Component(modules = {ClientModule.class, DeleteTopicModule.class})
interface DeleteTopicComponent {

  DeleteTopicCommandFactory getDeleteTopicCommandFactory();

  Logger getLogger();
}
