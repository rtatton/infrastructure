package org.cirrus.infrastructure.handler;

import org.cirrus.infrastructure.handler.api.ApiCommand;
import org.cirrus.infrastructure.handler.api.ApiRequest;
import org.cirrus.infrastructure.handler.api.ApiResponse;

public class UploadCodeHandler extends AbstractHandler {

  private static final ApiCommand command = UploadCodeApi.create();

  @Override
  protected ApiResponse handle(ApiRequest request) {
    return command.run(request);
  }
}
