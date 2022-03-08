package org.cirrus.infrastructure.handler;

import java.io.UncheckedIOException;
import javax.inject.Inject;
import org.cirrus.infrastructure.handler.api.ApiCommand;
import org.cirrus.infrastructure.handler.api.ApiRequest;
import org.cirrus.infrastructure.handler.api.ApiResponse;
import org.cirrus.infrastructure.handler.api.HttpStatus;
import org.cirrus.infrastructure.handler.exception.FailedResourceDeletionException;
import org.cirrus.infrastructure.handler.exception.FailedStorageDeleteException;
import org.cirrus.infrastructure.handler.exception.NoSuchNodeException;

final class DeleteNodeApi implements ApiCommand {

  private static final DeleteNodeComponent component = DaggerDeleteNodeComponent.create();
  private final Command<?, ?> command;

  @Inject
  public DeleteNodeApi(DeleteNodeCommand command) {
    this.command = command;
  }

  public static DeleteNodeApi create() {
    return component.api();
  }

  @Override
  public ApiResponse run(ApiRequest request) {
    String body;
    int status;
    try {
      body = command.runFromString(request.body());
      status = HttpStatus.OK;
    } catch (UncheckedIOException
        | NoSuchNodeException
        | FailedStorageDeleteException
        | FailedResourceDeletionException exception) {
      body = exception.getMessage();
      status = HttpStatus.BAD_REQUEST;
    }
    return ApiResponse.of(body, status);
  }
}
