package org.cirrus.infrastructure.handler;

import java.util.concurrent.CompletionException;
import javax.inject.Inject;
import org.cirrus.infrastructure.handler.exception.CirrusException;
import org.cirrus.infrastructure.handler.exception.FailedCodeUploadException;
import org.cirrus.infrastructure.handler.exception.FailedMappingException;
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

  /**
   * @param input A JSON-formatted {@link UploadCodeRequest}
   * @return A JSON-formatted {@link UploadCodeResponse}
   * @throws FailedMappingException Thrown when the input fails to be converted into a {@link
   *     UploadCodeRequest} instance, or the output fails to be converted into a {@link
   *     UploadCodeResponse} instance.
   * @see UploadCodeCommand#run(UploadCodeRequest)
   */
  @Override
  public String runFromString(String input) {
    UploadCodeRequest request = mapper.read(input, UploadCodeRequest.class);
    UploadCodeResponse response = run(request);
    return mapper.write(response);
  }
}
