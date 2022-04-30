package org.cirrus.infrastructure.handler;

import javax.inject.Inject;
import org.cirrus.infrastructure.handler.api.ApiCommand;
import org.cirrus.infrastructure.handler.api.ApiRequest;
import org.cirrus.infrastructure.handler.api.ApiResponse;
import org.cirrus.infrastructure.handler.api.HttpStatus;
import org.cirrus.infrastructure.handler.exception.CirrusException;
import org.cirrus.infrastructure.handler.util.Mapper;

public final class DeleteNodeApiCommand implements ApiCommand {

  private static final DeleteNodeComponent component = DaggerDeleteNodeComponent.create();
  private final Command<DeleteNodeRequest, DeleteNodeResponse> command;
  private final Mapper mapper;

  @Inject
  DeleteNodeApiCommand(Command<DeleteNodeRequest, DeleteNodeResponse> command, Mapper mapper) {
    this.command = command;
    this.mapper = mapper;
  }

  public static DeleteNodeApiCommand create() {
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
    DeleteNodeRequest request = mapper.read(body, DeleteNodeRequest.class);
    DeleteNodeResponse response = command.run(request);
    return mapper.write(response);
  }
}
