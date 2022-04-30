package org.cirrus.infrastructure.handler;

import javax.inject.Inject;
import org.cirrus.infrastructure.handler.api.ApiCommand;
import org.cirrus.infrastructure.handler.api.ApiRequest;
import org.cirrus.infrastructure.handler.api.ApiResponse;
import org.cirrus.infrastructure.handler.api.HttpStatus;
import org.cirrus.infrastructure.handler.exception.CirrusException;
import org.cirrus.infrastructure.handler.util.Mapper;

public final class UploadCodeApiCommand implements ApiCommand {

  private static final UploadCodeComponent component = DaggerUploadCodeComponent.create();
  private final Command<UploadCodeRequest, UploadCodeResponse> command;
  private final Mapper mapper;

  @Inject
  UploadCodeApiCommand(Command<UploadCodeRequest, UploadCodeResponse> command, Mapper mapper) {
    this.command = command;
    this.mapper = mapper;
  }

  public static UploadCodeApiCommand create() {
    return component.api();
  }

  @Override
  public ApiResponse run(ApiRequest request) {
    String body;
    int status;
    try {
      body = run(request.body());
      status = HttpStatus.OK;
    } catch (CirrusException exception) {
      body = exception.getMessage();
      status = HttpStatus.BAD_REQUEST;
    }
    return ApiResponse.of(body, status);
  }

  private String run(String body) {
    UploadCodeRequest request = mapper.read(body, UploadCodeRequest.class);
    UploadCodeResponse response = command.run(request);
    return mapper.write(response);
  }
}
