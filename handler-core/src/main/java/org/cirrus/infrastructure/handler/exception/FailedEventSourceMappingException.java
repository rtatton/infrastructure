package org.cirrus.infrastructure.handler.exception;

public class FailedEventSourceMappingException extends RuntimeException {

  public FailedEventSourceMappingException() {}

  public FailedEventSourceMappingException(String message) {
    super(message);
  }

  public FailedEventSourceMappingException(String message, Throwable cause) {
    super(message, cause);
  }

  public FailedEventSourceMappingException(Throwable cause) {
    super(cause);
  }
}
