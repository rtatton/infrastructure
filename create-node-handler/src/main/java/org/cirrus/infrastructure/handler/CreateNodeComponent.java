package org.cirrus.infrastructure.handler;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = {HandlerModule.class, CreateNodeBindings.class})
interface CreateNodeComponent {

  CreateNodeApiCommand api();
}
