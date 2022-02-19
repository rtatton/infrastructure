package org.cirrus.infrastructure.handler.exception;

public class FailedResourceDeletionException extends RuntimeException {

  public FailedResourceDeletionException() {}

  public FailedResourceDeletionException(Throwable cause) {
    super(cause);
  }
}
