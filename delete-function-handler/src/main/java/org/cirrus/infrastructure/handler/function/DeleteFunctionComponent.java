package org.cirrus.infrastructure.handler.function;

import dagger.Component;
import javax.inject.Singleton;
import org.cirrus.infrastructure.util.ClientModule;
import org.cirrus.infrastructure.util.Logger;

@Singleton
@Component(modules = {ClientModule.class, DeleteFunctionModule.class})
interface DeleteFunctionComponent {

  DeleteFunctionCommandFactory getDeleteFunctionCommandFactory();

  Logger getLogger();
}
