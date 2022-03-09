package org.cirrus.infrastructure.handler.exception;

public class FailedStorageWriteException extends CirrusException {

  public FailedStorageWriteException() {}

  public FailedStorageWriteException(String message) {
    super(message);
  }

  public FailedStorageWriteException(String message, Throwable cause) {
    super(message, cause);
  }

  public FailedStorageWriteException(Throwable cause) {
    super(cause);
  }
}
