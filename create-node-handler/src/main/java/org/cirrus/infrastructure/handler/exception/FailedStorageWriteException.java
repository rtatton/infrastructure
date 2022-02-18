package org.cirrus.infrastructure.handler.exception;

public class FailedStorageWriteException extends RuntimeException {

  public FailedStorageWriteException(Throwable cause) {
    super(cause);
  }
}
