package org.cirrus.infrastructure.handler;

import java.util.concurrent.CompletionException;
import javax.inject.Inject;
import org.cirrus.infrastructure.handler.exception.CirrusException;
import org.cirrus.infrastructure.handler.exception.FailedCodePublicationException;
import org.cirrus.infrastructure.handler.exception.FailedMappingException;
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

  /**
   * Publishes uploaded code that defines the behavior of a cloud-based node so that it can be
   * consumed as an artifact when creating nodes.
   *
   * @param request A request that contains the uploaded code identifier and runtime.
   * @return A response containing the published artifact identifier.
   * @throws FailedCodePublicationException Thrown when an error occurs that is related to the
   *     publication of the uploaded code.
   * @throws CirrusException Thrown when any unknown exception occurs.
   */
  @Override
  public PublishCodeResponse run(PublishCodeRequest request) {
    try {
      String artifactId = functionService.publishCode(request.codeId(), request.runtime()).join();
      return PublishCodeResponse.of(artifactId);
    } catch (CompletionException exception) {
      throw CirrusException.cast(exception.getCause());
    }
  }

  /**
   * @param input A JSON-formatted {@link PublishCodeRequest}
   * @return A JSON-formatted {@link PublishCodeResponse}
   * @throws FailedMappingException Thrown when the input fails to be converted into a {@link
   *     PublishCodeRequest} instance, or the output fails to be converted into a {@link
   *     PublishCodeResponse} instance.
   * @see PublishCodeCommand#run(PublishCodeRequest)
   */
  @Override
  public String runFromString(String input) {
    PublishCodeRequest request = mapper.read(input, PublishCodeRequest.class);
    PublishCodeResponse response = run(request);
    return mapper.write(response);
  }
}
