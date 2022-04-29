package org.cirrus.infrastructure.handler;

import java.util.concurrent.CompletionException;
import javax.inject.Inject;
import org.cirrus.infrastructure.handler.exception.CirrusException;
import org.cirrus.infrastructure.handler.exception.FailedCodeUploadException;
import org.cirrus.infrastructure.handler.service.FunctionService;
import org.cirrus.infrastructure.handler.util.Resources;

public final class UploadCodeCommand implements Command<UploadCodeRequest, UploadCodeResponse> {

  private final FunctionService functionService;

  @Inject
  public UploadCodeCommand(FunctionService functionService) {
    this.functionService = functionService;
  }

  /**
   * Generates a URL to which code can be uploaded, along with a unique code identifier that can be
   * used to reference the code for future requests.
   *
   * @param request An empty request.
   * @return A response containing the upload URL and code identifier.
   * @throws FailedCodeUploadException Thrown when an error occurred when generating an upload URL.
   * @throws CirrusException Thrown when any unknown exception occurs.
   */
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
}
