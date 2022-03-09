package org.cirrus.infrastructure.handler;

import javax.inject.Inject;
import org.cirrus.infrastructure.handler.service.FunctionService;
import org.cirrus.infrastructure.handler.util.Mapper;
import org.cirrus.infrastructure.handler.util.Resources;

final class UploadCodeCommand implements Command<UploadCodeRequest, UploadCodeResponse> {

  private final FunctionService functionService;
  private final Mapper mapper;

  @Inject
  public UploadCodeCommand(FunctionService functionService, Mapper mapper) {
    this.functionService = functionService;
    this.mapper = mapper;
  }

  @Override
  public UploadCodeResponse run(UploadCodeRequest request) {
    String key = Resources.createRandomId();
    String url = functionService.getUploadUrl(key).toCompletableFuture().join();
    return UploadCodeResponse.builder().codeKey(key).uploadUrl(url).build();
  }

  @Override
  public String runFromString(String input) {
    UploadCodeRequest request = mapper.read(input, UploadCodeRequest.class);
    UploadCodeResponse response = run(request);
    return mapper.write(response);
  }
}
