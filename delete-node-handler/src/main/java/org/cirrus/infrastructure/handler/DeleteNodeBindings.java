package org.cirrus.infrastructure.handler;

import dagger.Binds;
import dagger.Module;
import javax.inject.Singleton;

@Module
interface DeleteNodeBindings {

  @Binds
  @Singleton
  Command<DeleteNodeRequest, DeleteNodeResponse> command(DeleteNodeCommand command);
}
