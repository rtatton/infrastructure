package org.cirrus.infrastructure.handler;

import javax.inject.Inject;
import org.cirrus.infrastructure.handler.service.FunctionService;
import org.cirrus.infrastructure.handler.util.Mapper;

final class PublishCodeCommand implements Command<PublishCodeRequest, PublishCodeResponse> {

  private final FunctionService functionService;
  private final Mapper mapper;

  @Inject
  public PublishCodeCommand(FunctionService functionService, Mapper mapper) {
    this.functionService = functionService;
    this.mapper = mapper;
  }

  @Override
  public PublishCodeResponse run(PublishCodeRequest request) {
    String codeUri = functionService.publishCode(request.codeKey(), request.runtime()).join();
    return PublishCodeResponse.of(codeUri);
  }

  @Override
  public String runFromString(String input) {
    PublishCodeRequest request = mapper.read(input, PublishCodeRequest.class);
    PublishCodeResponse response = run(request);
    return mapper.write(response);
  }
}
