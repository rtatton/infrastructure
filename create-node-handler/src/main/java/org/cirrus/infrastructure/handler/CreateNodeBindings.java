package org.cirrus.infrastructure.handler;

import dagger.Binds;
import dagger.Module;
import javax.inject.Singleton;
import org.cirrus.infrastructure.util.ConsoleLogger;
import org.cirrus.infrastructure.util.JacksonMapper;
import org.cirrus.infrastructure.util.Logger;
import org.cirrus.infrastructure.util.Mapper;

@Module
interface CreateNodeBindings {

  @Binds
  @Singleton
  Mapper bindMapper(JacksonMapper mapper);

  @Binds
  @Singleton
  Logger bindLogger(ConsoleLogger logger);
}
