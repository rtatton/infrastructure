package org.cirrus.infrastructure.handler;

import java.util.concurrent.CompletionException;
import javax.inject.Inject;
import org.cirrus.infrastructure.handler.exception.CirrusException;
import org.cirrus.infrastructure.handler.service.FunctionService;
import org.cirrus.infrastructure.handler.util.Mapper;

public class PublishCodeCommand implements Command<PublishCodeRequest, PublishCodeResponse> {

  private final FunctionService functionService;
  private final Mapper mapper;

  @Inject
  public PublishCodeCommand(FunctionService functionService, Mapper mapper) {
    this.functionService = functionService;
    this.mapper = mapper;
  }

  @Override
  public PublishCodeResponse run(PublishCodeRequest request) {
    try {
      String artifactId = functionService.publishCode(request.codeId(), request.runtime()).join();
      return PublishCodeResponse.of(artifactId);
    } catch (CompletionException exception) {
      throw CirrusException.cast(exception.getCause());
    }
  }

  @Override
  public String runFromString(String input) {
    PublishCodeRequest request = mapper.read(input, PublishCodeRequest.class);
    PublishCodeResponse response = run(request);
    return mapper.write(response);
  }
}
