package org.cirrus.infrastructure.handler.util;

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

  public static <T> String getOrNull(Future<T> future, Function<T, String> mapper, Logger logger) {
    String resourceId = null;
    try {
      resourceId = mapper.apply(future.get());
    } catch (InterruptedException | ExecutionException exception) {
      logger.error(exception.getLocalizedMessage());
    }
    return resourceId;
  }

  public static <T> void logIfError(Future<T> future, Logger logger) {
    try {
      future.get();
    } catch (InterruptedException | ExecutionException exception) {
      logger.error(exception.getLocalizedMessage());
    }
  }
}
