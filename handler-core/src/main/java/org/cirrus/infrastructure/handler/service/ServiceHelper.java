package org.cirrus.infrastructure.handler.service;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.cirrus.infrastructure.handler.exception.FailedResourceCreationException;
import org.cirrus.infrastructure.handler.model.Resource;
import org.cirrus.infrastructure.logging.Logger;

@Singleton
final class ServiceHelper {

  private final Logger logger;

  @Inject
  public ServiceHelper(Logger logger) {
    this.logger = logger;
  }

  public <T> CompletionStage<Resource> createResource(
      CompletionStage<T> stage, Function<T, String> getId) {
    return stage.handleAsync(
        (response, throwable) -> {
          Resource resource;
          if (throwable == null) {
            resource = Resource.builder().id(getId.apply(response)).build();
          } else {
            logger.error(throwable.getLocalizedMessage());
            RuntimeException exception = new FailedResourceCreationException(throwable);
            resource = Resource.builder().exception(exception).build();
          }
          return resource;
        });
  }

  public <T> CompletionStage<T> wrapThrowable(
      CompletionStage<T> stage, Function<Throwable, RuntimeException> mapToRuntimeException) {
    return stage.handleAsync(
        (response, throwable) -> {
          if (throwable != null) {
            logger.error(throwable.getLocalizedMessage());
            throw mapToRuntimeException.apply(throwable);
          }
          return response;
        });
  }
}
