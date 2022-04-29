package org.cirrus.infrastructure.handler;

import java.util.concurrent.CompletionException;
import javax.inject.Inject;
import org.cirrus.infrastructure.handler.exception.CirrusException;
import org.cirrus.infrastructure.handler.exception.FailedCodePublicationException;
import org.cirrus.infrastructure.handler.service.FunctionService;

public final class PublishCodeCommand implements Command<PublishCodeRequest, PublishCodeResponse> {

  private final FunctionService functionService;

  @Inject
  public PublishCodeCommand(FunctionService functionService) {
    this.functionService = functionService;
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
}
