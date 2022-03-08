package org.cirrus.infrastructure.handler;

import java.io.UncheckedIOException;
import javax.inject.Inject;
import org.cirrus.infrastructure.handler.api.ApiCommand;
import org.cirrus.infrastructure.handler.api.ApiRequest;
import org.cirrus.infrastructure.handler.api.ApiResponse;
import org.cirrus.infrastructure.handler.api.HttpStatus;

public class UploadCodeApi implements ApiCommand {

  private static final UploadCodeComponent component = DaggerUploadCodeComponent.create();
  private final UploadCodeCommand command;

  @Inject
  public UploadCodeApi(UploadCodeCommand command) {
    this.command = command;
  }

  public static UploadCodeApi create() {
    return component.api();
  }

  @Override
  public ApiResponse run(ApiRequest request) {
    String body;
    int status;
    try {
      body = command.runFromString(request.body());
      status = HttpStatus.OK;
    } catch (UncheckedIOException exception) {
      body = exception.getMessage();
      status = HttpStatus.BAD_REQUEST;
    }
    return ApiResponse.of(body, status);
  }
}
