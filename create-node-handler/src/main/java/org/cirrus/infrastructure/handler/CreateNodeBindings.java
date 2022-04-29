package org.cirrus.infrastructure.handler;

import dagger.Binds;
import dagger.Module;
import javax.inject.Singleton;

@Module
interface CreateNodeBindings {

  @Binds
  @Singleton
  Command<CreateNodeRequest, CreateNodeResponse> command(CreateNodeCommand command);
}
