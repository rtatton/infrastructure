package org.cirrus.infrastructure.handler.exception;

public class FailedStorageReadException extends RuntimeException {

  public FailedStorageReadException() {}

  public FailedStorageReadException(String message) {
    super(message);
  }

  public FailedStorageReadException(String message, Throwable cause) {
    super(message, cause);
  }

  public FailedStorageReadException(Throwable cause) {
    super(cause);
  }
}
