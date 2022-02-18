package org.cirrus.infrastructure.handler.exception;

public class FailedResourceCreationException extends RuntimeException {

  public FailedResourceCreationException(Throwable cause) {
    super(cause);
  }
}
