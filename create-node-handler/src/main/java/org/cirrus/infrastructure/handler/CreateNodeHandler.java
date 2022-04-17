package org.cirrus.infrastructure.handler;

import org.cirrus.infrastructure.handler.api.ApiCommand;
import org.cirrus.infrastructure.handler.api.ApiHandler;
import org.cirrus.infrastructure.handler.api.ApiRequest;
import org.cirrus.infrastructure.handler.api.ApiResponse;

public class CreateNodeHandler extends ApiHandler {

  private static final ApiCommand command = CreateNodeApi.create();
  private static final HandlerComponent component = DaggerHandlerComponent.create();

  public CreateNodeHandler() {
    super(component.mapper(), component.logger());
  }

  @Override
  public ApiResponse run(ApiRequest request) {
    return command.run(request);
  }
}
