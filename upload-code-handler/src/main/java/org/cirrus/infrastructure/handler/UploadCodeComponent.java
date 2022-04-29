package org.cirrus.infrastructure.handler;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = {HandlerModule.class, UploadCodeBindings.class})
interface UploadCodeComponent {

  UploadCodeApi api();
}
