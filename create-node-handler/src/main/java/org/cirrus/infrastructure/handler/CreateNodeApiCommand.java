package org.cirrus.infrastructure.handler;

import javax.inject.Inject;
import org.cirrus.infrastructure.handler.api.ApiCommand;
import org.cirrus.infrastructure.handler.api.ApiRequest;
import org.cirrus.infrastructure.handler.api.ApiResponse;
import org.cirrus.infrastructure.handler.api.HttpStatus;
import org.cirrus.infrastructure.handler.exception.CirrusException;
import org.cirrus.infrastructure.handler.util.Mapper;

public final class CreateNodeApiCommand implements ApiCommand {

  private static final CreateNodeComponent component = DaggerCreateNodeComponent.create();
  private final Command<CreateNodeRequest, CreateNodeResponse> command;
  private final Mapper mapper;

  @Inject
  CreateNodeApiCommand(Command<CreateNodeRequest, CreateNodeResponse> command, Mapper mapper) {
    this.command = command;
    this.mapper = mapper;
  }

  public static CreateNodeApiCommand create() {
    return component.api();
  }

  @Override
  public ApiResponse run(ApiRequest request) {
    String body;
    int status;
    try {
      body = run(request.body());
      status = HttpStatus.CREATED;
    } catch (CirrusException exception) {
      body = exception.getMessage();
      status = HttpStatus.BAD_REQUEST;
    }
    return ApiResponse.of(body, status);
  }

  private String run(String body) {
    CreateNodeRequest request = mapper.read(body, CreateNodeRequest.class);
    CreateNodeResponse response = command.run(request);
    return mapper.write(response);
  }
}
