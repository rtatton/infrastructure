package org.cirrus.infrastructure.handler;

import javax.inject.Inject;
import org.cirrus.infrastructure.handler.api.ApiCommand;
import org.cirrus.infrastructure.handler.api.ApiRequest;
import org.cirrus.infrastructure.handler.api.ApiResponse;
import org.cirrus.infrastructure.handler.api.HttpStatus;
import org.cirrus.infrastructure.handler.exception.CirrusException;
import org.cirrus.infrastructure.handler.util.Mapper;

public class UploadCodeApi implements ApiCommand {

  private static final UploadCodeComponent component = DaggerUploadCodeComponent.create();
  private final Command<UploadCodeRequest, UploadCodeResponse> command;
  private final Mapper mapper;

  @Inject
  public UploadCodeApi(UploadCodeCommand command, Mapper mapper) {
    this.command = command;
    this.mapper = mapper;
  }

  public static UploadCodeApi create() {
    return component.api();
  }

  @Override
  public ApiResponse run(ApiRequest request) {
    String body;
    try {
      UploadCodeRequest mapped = mapper.read(request.body(), UploadCodeRequest.class);
      UploadCodeResponse response = command.run(mapped);
      body = mapper.write(response);
    } catch (CirrusException exception) {
      body = exception.getMessage();
    }
    return ApiResponse.of(body, HttpStatus.CREATED);
  }
}
