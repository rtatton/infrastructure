package org.cirrus.infrastructure.handler.exception;

public class FailedResourceDeletionException extends RuntimeException {

  public FailedResourceDeletionException() {}

  public FailedResourceDeletionException(String message) {
    super(message);
  }

  public FailedResourceDeletionException(String message, Throwable cause) {
    super(message, cause);
  }

  public FailedResourceDeletionException(Throwable cause) {
    super(cause);
  }
}
