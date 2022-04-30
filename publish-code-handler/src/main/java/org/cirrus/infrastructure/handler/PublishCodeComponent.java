package org.cirrus.infrastructure.handler;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = {HandlerModule.class, PublishCodeBindings.class})
interface PublishCodeComponent {

  PublishCodeApiCommand api();
}
