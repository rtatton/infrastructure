package org.cirrus.infrastructure.handler;

import org.cirrus.infrastructure.handler.api.ApiCommand;
import org.cirrus.infrastructure.handler.api.ApiRequest;
import org.cirrus.infrastructure.handler.api.ApiResponse;

public class CreateNodeHandler extends AbstractHandler {

  private static final ApiCommand command = CreateNodeApi.create();

  @Override
  protected ApiResponse handle(ApiRequest request) {
    return command.run(request);
  }
}
