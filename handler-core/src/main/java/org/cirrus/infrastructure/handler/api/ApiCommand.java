package org.cirrus.infrastructure.handler.api;

import org.cirrus.infrastructure.handler.Command;

public interface ApiCommand extends Command<ApiRequest, ApiResponse> {

  @Override
  ApiResponse run(ApiRequest request);

  @Override
  default String runFromString(String request) {
    throw new UnsupportedOperationException();
  }
}
