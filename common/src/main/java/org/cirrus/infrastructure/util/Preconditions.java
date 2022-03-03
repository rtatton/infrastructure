package org.cirrus.infrastructure.util;

public final class Preconditions {

  private Preconditions() {
    // no-op
  }

  public static void checkState(boolean expression) {
    if (!expression) {
      throw new IllegalStateException();
    }
  }

  public static <T> T checkNotNull(T object) {
    if (object == null) {
      throw new NullPointerException();
    }
    return object;
  }

  public static Number inRangeClosed(double value, double upper, double lower) {
    checkState(value <= upper && value >= lower);
    return value;
  }

  public static String notNullOrEmpty(String string) {
    checkState(string != null && !string.isEmpty());
    return string;
  }

  public static <T> T notNull(T object) {
    return checkNotNull(object);
  }
}
