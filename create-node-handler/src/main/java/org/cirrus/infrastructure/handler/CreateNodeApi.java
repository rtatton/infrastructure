package org.cirrus.infrastructure.handler;

import org.cirrus.infrastructure.handler.api.ApiCommand;
import org.cirrus.infrastructure.handler.api.ApiRequest;
import org.cirrus.infrastructure.handler.api.ApiResponse;
import org.cirrus.infrastructure.handler.api.HttpStatus;
import org.cirrus.infrastructure.handler.exception.FailedEventSourceMappingException;
import org.cirrus.infrastructure.handler.exception.FailedResourceCreationException;
import org.cirrus.infrastructure.handler.exception.FailedResourceDeletionException;
import org.cirrus.infrastructure.handler.exception.FailedStorageReadException;
import org.cirrus.infrastructure.handler.exception.FailedStorageWriteException;
import org.cirrus.infrastructure.handler.exception.NoSuchNodeException;

public final class CreateNodeApi implements ApiCommand {

  private final Command<?, ?> command;

  private CreateNodeApi() {
    this.command = CreateNodeCommand.create();
  }

  public static CreateNodeApi create() {
    return new CreateNodeApi();
  }

  @Override
  public ApiResponse run(ApiRequest request) {
    String body;
    int status;
    try {
      body = command.runFromString(request.body());
      status = HttpStatus.CREATED;
    } catch (NoSuchNodeException
        | FailedStorageReadException
        | FailedResourceCreationException
        | FailedResourceDeletionException
        | FailedEventSourceMappingException
        | FailedStorageWriteException exception) {
      body = exception.getMessage();
      status = HttpStatus.BAD_REQUEST;
    }
    return ApiResponse.of(body, status);
  }
}
