package org.cirrus.infrastructure.handler;

import java.util.concurrent.CompletionException;
import javax.inject.Inject;
import org.cirrus.infrastructure.handler.exception.CirrusException;
import org.cirrus.infrastructure.handler.service.FunctionService;
import org.cirrus.infrastructure.handler.util.Mapper;
import org.cirrus.infrastructure.handler.util.Resources;

public class UploadCodeCommand implements Command<UploadCodeRequest, UploadCodeResponse> {

  private final FunctionService functionService;
  private final Mapper mapper;

  @Inject
  public UploadCodeCommand(FunctionService functionService, Mapper mapper) {
    this.functionService = functionService;
    this.mapper = mapper;
  }

  @Override
  public UploadCodeResponse run(UploadCodeRequest request) {
    try {
      String codeId = Resources.createRandomId();
      String uploadUrl = functionService.getUploadUrl(codeId).join();
      return UploadCodeResponse.builder().codeId(codeId).uploadUrl(uploadUrl).build();
    } catch (CompletionException exception) {
      throw CirrusException.cast(exception.getCause());
    }
  }

  @Override
  public String runFromString(String input) {
    UploadCodeRequest request = mapper.read(input, UploadCodeRequest.class);
    UploadCodeResponse response = run(request);
    return mapper.write(response);
  }
}
