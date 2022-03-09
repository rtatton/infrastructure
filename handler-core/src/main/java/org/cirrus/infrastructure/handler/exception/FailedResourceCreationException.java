package org.cirrus.infrastructure.handler.exception;

public class FailedResourceCreationException extends CirrusException {

  public FailedResourceCreationException() {}

  public FailedResourceCreationException(String message) {
    super(message);
  }

  public FailedResourceCreationException(String message, Throwable cause) {
    super(message, cause);
  }

  public FailedResourceCreationException(Throwable cause) {
    super(cause);
  }
}
