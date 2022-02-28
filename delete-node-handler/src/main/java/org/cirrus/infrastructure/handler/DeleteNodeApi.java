package org.cirrus.infrastructure.handler;

import org.cirrus.infrastructure.handler.api.ApiCommand;
import org.cirrus.infrastructure.handler.api.ApiRequest;
import org.cirrus.infrastructure.handler.api.ApiResponse;
import org.cirrus.infrastructure.handler.api.HttpStatus;
import org.cirrus.infrastructure.handler.exception.FailedResourceDeletionException;
import org.cirrus.infrastructure.handler.exception.FailedStorageDeleteException;
import org.cirrus.infrastructure.handler.exception.NoSuchNodeException;

public final class DeleteNodeApi implements ApiCommand {

  private final Command<?, ?> command;

  private DeleteNodeApi() {
    this.command = DeleteNodeCommand.create();
  }

  public static DeleteNodeApi create() {
    return new DeleteNodeApi();
  }

  @Override
  public ApiResponse run(ApiRequest request) {
    String body;
    int status;
    try {
      body = command.runFromString(request.body());
      status = HttpStatus.OK;
    } catch (NoSuchNodeException
        | FailedStorageDeleteException
        | FailedResourceDeletionException exception) {
      body = exception.getMessage();
      status = HttpStatus.BAD_REQUEST;
    }
    return ApiResponse.of(body, status);
  }
}
