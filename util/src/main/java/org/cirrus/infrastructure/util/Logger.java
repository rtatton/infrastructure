package org.cirrus.infrastructure.util;

public interface Logger {

  static Logger of(String name) {
    return new Slf4jLogger(name);
  }

  default void debug(String format, Object... args) {
    debug(String.format(format, args));
  }

  void debug(String message);

  default void info(String format, Object... args) {
    info(String.format(format, args));
  }

  void info(String message);

  default void warn(String format, Object... args) {
    warn(String.format(format, args));
  }

  void warn(String message);

  default void error(String format, Object... args) {
    error(String.format(format, args));
  }

  void error(String message);
}
