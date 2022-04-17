package org.cirrus.infrastructure.handler;

import dagger.Component;
import javax.inject.Singleton;
import org.cirrus.infrastructure.handler.util.Logger;
import org.cirrus.infrastructure.handler.util.Mapper;

@Singleton
@Component(modules = HandlerModule.class)
public interface HandlerComponent {

  Mapper mapper();

  Logger logger();
}
