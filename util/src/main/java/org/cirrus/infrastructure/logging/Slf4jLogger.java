package org.cirrus.infrastructure.logging;

import org.slf4j.LoggerFactory;

public class Slf4jLogger implements Logger {

  private final org.slf4j.Logger logger;

  public Slf4jLogger(String name) {
    this.logger = LoggerFactory.getLogger(name);
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
