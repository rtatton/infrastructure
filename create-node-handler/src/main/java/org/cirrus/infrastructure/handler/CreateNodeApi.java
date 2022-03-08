package org.cirrus.infrastructure.handler;

import java.io.UncheckedIOException;
import javax.inject.Inject;
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

final class CreateNodeApi implements ApiCommand {

  private static final CreateNodeComponent component = DaggerCreateNodeComponent.create();
  private final Command<?, ?> command;

  @Inject
  public CreateNodeApi(CreateNodeCommand command) {
    this.command = command;
  }

  public static CreateNodeApi create() {
    return component.api();
  }

  @Override
  public ApiResponse run(ApiRequest request) {
    String body;
    int status;
    try {
      body = command.runFromString(request.body());
      status = HttpStatus.CREATED;
    } catch (UncheckedIOException
        | NoSuchNodeException
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
