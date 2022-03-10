package org.cirrus.infrastructure.handler.service;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.cirrus.infrastructure.handler.exception.CirrusException;
import org.cirrus.infrastructure.handler.util.Logger;

@Singleton
final class ServiceHelper {

  private final Logger logger;

  @Inject
  public ServiceHelper(Logger logger) {
    this.logger = logger;
  }

  public <T, U> CompletableFuture<U> getOrThrow(
      CompletableFuture<T> future,
      Function<T, U> mapResult,
      Function<Throwable, CirrusException> mapThrowable) {
    return wrapThrowable(future, mapThrowable).thenApplyAsync(mapResult);
  }

  public <T> CompletableFuture<Void> getOrThrow(
      CompletableFuture<T> future, Function<Throwable, CirrusException> mapThrowable) {
    return getOrThrow(future, x -> null, mapThrowable);
  }

  public <T> CompletableFuture<T> wrapThrowable(
      CompletableFuture<T> future, Function<Throwable, CirrusException> mapThrowable) {
    return future.handleAsync(
        (response, throwable) -> {
          if (throwable != null) {
            logger.error(throwable.getLocalizedMessage());
            throw mapThrowable.apply(throwable);
          }
          return response;
        });
  }
}
