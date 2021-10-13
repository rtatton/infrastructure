package org.cirrus.infrastructure.handler.function;

import dagger.Component;
import javax.inject.Singleton;
import org.cirrus.infrastructure.handler.util.ClientModule;
import org.cirrus.infrastructure.handler.util.Logger;

@Singleton
@Component(modules = {ClientModule.class, CreateFunctionModule.class})
interface CreateFunctionComponent {

  CreateFunctionCommand getCreateFunctionCommand();

  Logger getLogger();
}
