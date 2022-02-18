package org.cirrus.infrastructure.handler.exception;

public class FailedEventSourceMappingException extends RuntimeException {

  public FailedEventSourceMappingException(Throwable cause) {
    super(cause);
  }
}
