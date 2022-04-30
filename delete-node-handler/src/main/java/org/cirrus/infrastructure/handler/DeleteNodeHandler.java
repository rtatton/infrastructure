package org.cirrus.infrastructure.handler;

import org.cirrus.infrastructure.handler.api.ApiCommand;
import org.cirrus.infrastructure.handler.api.ApiHandler;

public final class DeleteNodeHandler extends ApiHandler {

  private static final ApiCommand command = DeleteNodeApiCommand.create();
  private static final HandlerComponent component = DaggerHandlerComponent.create();

  public DeleteNodeHandler() {
    super(command, component.mapper(), component.logger());
  }
}
