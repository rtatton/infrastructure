package org.cirrus.infrastructure.handler.exception;

public class FailedEventSourceMappingException extends RuntimeException {

  public FailedEventSourceMappingException() {}

  public FailedEventSourceMappingException(Throwable cause) {
    super(cause);
  }
}
