package org.cirrus.infrastructure.handler;

import dagger.Binds;
import dagger.Module;
import javax.inject.Singleton;

@Module
interface PublishCodeBindings {

  @Binds
  @Singleton
  Command<PublishCodeRequest, PublishCodeResponse> command(PublishCodeCommand command);
}
