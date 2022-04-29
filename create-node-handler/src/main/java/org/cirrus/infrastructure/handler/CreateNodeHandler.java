package org.cirrus.infrastructure.handler;

import org.cirrus.infrastructure.handler.api.ApiCommand;
import org.cirrus.infrastructure.handler.api.ApiHandler;

public final class CreateNodeHandler extends ApiHandler {

  private static final ApiCommand command = CreateNodeApi.create();
  private static final HandlerComponent component = DaggerHandlerComponent.create();

  public CreateNodeHandler() {
    super(command, component.mapper(), component.logger());
  }
}
