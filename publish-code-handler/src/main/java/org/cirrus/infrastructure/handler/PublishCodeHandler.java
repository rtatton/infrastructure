package org.cirrus.infrastructure.handler;

import org.cirrus.infrastructure.handler.api.ApiCommand;
import org.cirrus.infrastructure.handler.api.ApiHandler;
import org.cirrus.infrastructure.handler.api.ApiRequest;
import org.cirrus.infrastructure.handler.api.ApiResponse;

public class PublishCodeHandler extends ApiHandler {

  private static final ApiCommand command = PublishCodeApi.create();

  @Override
  public ApiResponse run(ApiRequest request) {
    return command.run(request);
  }
}
