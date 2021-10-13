package org.cirrus.infrastructure.handler.util;

import org.slf4j.LoggerFactory;

public final class DefaultLogger implements Logger {

  private final org.slf4j.Logger logger;

  private DefaultLogger(String name) {
    this.logger = LoggerFactory.getLogger(name);
  }

  public static DefaultLogger of(String name) {
    return new DefaultLogger(name);
  }

  @Override
  public void debug(String message) {
    logger.debug(message);
  }

  @Override
  public void info(String message) {
    logger.info(message);
  }

  @Override
  public void warn(String message) {
    logger.warn(message);
  }

  @Override
  public void error(String message) {
    logger.error(message);
  }
}
