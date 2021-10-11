package org.cirrus.infrastructure.task.util;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Resources {

  public static String createRandomId() {
    return UUID.randomUUID().toString();
  }

  public static <T> String getIdOrNull(
      Future<T> future, Function<T, String> converter, Consumer<Throwable> logger) {
    String resourceId = null;
    try {
      resourceId = converter.apply(future.get());
    } catch (InterruptedException | ExecutionException exception) {
      logger.accept(exception);
    }
    return resourceId;
  }

  public static <T> void logIfError(Future<T> future, Consumer<Throwable> logger) {
    try {
      future.get();
    } catch (InterruptedException | ExecutionException exception) {
      logger.accept(exception);
    }
  }
}
