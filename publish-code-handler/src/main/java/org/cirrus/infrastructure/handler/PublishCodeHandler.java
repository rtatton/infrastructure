package org.cirrus.infrastructure.handler;

import org.cirrus.infrastructure.handler.api.ApiCommand;
import org.cirrus.infrastructure.handler.api.ApiHandler;

public final class PublishCodeHandler extends ApiHandler {

  private static final ApiCommand command = PublishCodeApi.create();
  private static final HandlerComponent component = DaggerHandlerComponent.create();

  public PublishCodeHandler() {
    super(command, component.mapper(), component.logger());
  }
}
