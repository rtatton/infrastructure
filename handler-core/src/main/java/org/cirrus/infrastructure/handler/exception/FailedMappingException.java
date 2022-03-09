package org.cirrus.infrastructure.handler.exception;

public class FailedMappingException extends CirrusException {

  public FailedMappingException() {}

  public FailedMappingException(String message) {
    super(message);
  }

  public FailedMappingException(String message, Throwable cause) {
    super(message, cause);
  }

  public FailedMappingException(Throwable cause) {
    super(cause);
  }
}
