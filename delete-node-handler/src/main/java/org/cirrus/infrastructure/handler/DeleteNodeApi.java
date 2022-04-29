package org.cirrus.infrastructure.handler;

import javax.inject.Inject;
import org.cirrus.infrastructure.handler.api.ApiCommand;
import org.cirrus.infrastructure.handler.api.ApiRequest;
import org.cirrus.infrastructure.handler.api.ApiResponse;
import org.cirrus.infrastructure.handler.api.HttpStatus;
import org.cirrus.infrastructure.handler.exception.CirrusException;
import org.cirrus.infrastructure.handler.util.Mapper;

public class DeleteNodeApi implements ApiCommand {

  private static final DeleteNodeComponent component = DaggerDeleteNodeComponent.create();
  private final Command<DeleteNodeRequest, DeleteNodeResponse> command;
  private final Mapper mapper;

  @Inject
  public DeleteNodeApi(DeleteNodeCommand command, Mapper mapper) {
    this.command = command;
    this.mapper = mapper;
  }

  public static DeleteNodeApi create() {
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
      status = HttpStatus.OK;
    }
    return ApiResponse.of(body, status);
  }

  private String run(String body) {
    DeleteNodeRequest mapped = mapper.read(body, DeleteNodeRequest.class);
    DeleteNodeResponse response = command.run(mapped);
    return mapper.write(response);
  }
}
