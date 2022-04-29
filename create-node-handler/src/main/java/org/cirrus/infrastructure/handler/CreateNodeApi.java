package org.cirrus.infrastructure.handler;

import javax.inject.Inject;
import org.cirrus.infrastructure.handler.api.ApiCommand;
import org.cirrus.infrastructure.handler.api.ApiRequest;
import org.cirrus.infrastructure.handler.api.ApiResponse;
import org.cirrus.infrastructure.handler.api.HttpStatus;
import org.cirrus.infrastructure.handler.exception.CirrusException;
import org.cirrus.infrastructure.handler.util.Mapper;

public class CreateNodeApi implements ApiCommand {

  private static final CreateNodeComponent component = DaggerCreateNodeComponent.create();
  private final Command<CreateNodeRequest, CreateNodeResponse> command;
  private final Mapper mapper;

  @Inject
  public CreateNodeApi(CreateNodeCommand command, Mapper mapper) {
    this.command = command;
    this.mapper = mapper;
  }

  public static CreateNodeApi create() {
    return component.api();
  }

  @Override
  public ApiResponse run(ApiRequest request) {
    String body;
    try {
      CreateNodeRequest mapped = mapper.read(request.body(), CreateNodeRequest.class);
      CreateNodeResponse response = command.run(mapped);
      body = mapper.write(response);
    } catch (CirrusException exception) {
      body = exception.getMessage();
    }
    return ApiResponse.of(body, HttpStatus.CREATED);
  }
}
