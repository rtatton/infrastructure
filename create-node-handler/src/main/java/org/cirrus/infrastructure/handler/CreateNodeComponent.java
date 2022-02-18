package org.cirrus.infrastructure.handler;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = CreateNodeModule.class)
interface CreateNodeComponent {

  CreateNodeCommand getCommand();
}
