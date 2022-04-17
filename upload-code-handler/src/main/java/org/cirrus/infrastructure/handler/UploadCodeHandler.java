package org.cirrus.infrastructure.handler;

import org.cirrus.infrastructure.handler.api.ApiCommand;
import org.cirrus.infrastructure.handler.api.ApiHandler;
import org.cirrus.infrastructure.handler.api.ApiRequest;
import org.cirrus.infrastructure.handler.api.ApiResponse;

public class UploadCodeHandler extends ApiHandler {

  private static final ApiCommand command = UploadCodeApi.create();
  private static final HandlerComponent component = DaggerHandlerComponent.create();

  public UploadCodeHandler() {
    super(component.mapper(), component.logger());
  }

  @Override
  public ApiResponse run(ApiRequest request) {
    return command.run(request);
  }
}
