package org.cirrus.infrastructure.handler;

import org.cirrus.infrastructure.handler.api.ApiCommand;
import org.cirrus.infrastructure.handler.api.ApiHandler;

public final class UploadCodeHandler extends ApiHandler {

  private static final ApiCommand command = UploadCodeApiCommand.create();
  private static final HandlerComponent component = DaggerHandlerComponent.create();

  public UploadCodeHandler() {
    super(command, component.mapper(), component.logger());
  }
}
