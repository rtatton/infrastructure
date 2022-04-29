package org.cirrus.infrastructure.handler;

import dagger.Binds;
import dagger.Module;
import javax.inject.Singleton;

@Module
interface UploadCodeBindings {

  @Binds
  @Singleton
  Command<UploadCodeRequest, UploadCodeResponse> command(UploadCodeCommand command);
}
