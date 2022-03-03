package org.cirrus.infrastructure.handler;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = HandlerModule.class)
interface DeleteNodeComponent {

  DeleteNodeApi api();
}
