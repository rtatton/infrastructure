package org.cirrus.infrastructure.util;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

public final class ResourceUtil {

  private ResourceUtil() {
    // No-op
  }

  public static String createRandomId() {
    return UUID.randomUUID().toString();
  }

  public static <T> T getOrThrow(Future<T> future, Logger logger, Throwable throwable) {
    return get(future, x -> x, logger).orElseThrow(() -> new RuntimeException(throwable));
  }

  public static <T, U> Optional<U> get(Future<T> future, Function<T, U> mapper, Logger logger) {
    U result = null;
    try {
      result = mapper.apply(future.get());
    } catch (InterruptedException | ExecutionException exception) {
      logger.error(exception.getLocalizedMessage());
    }
    return Optional.ofNullable(result);
  }

  public static <T> void logIfError(Future<T> future, Logger logger) {
    get(future, Function.identity(), logger);
  }
}
