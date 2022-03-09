package org.cirrus.infrastructure.handler.exception;

public class FailedStorageDeleteException extends CirrusException {

  public FailedStorageDeleteException() {}

  public FailedStorageDeleteException(String message) {
    super(message);
  }

  public FailedStorageDeleteException(String message, Throwable cause) {
    super(message, cause);
  }

  public FailedStorageDeleteException(Throwable cause) {
    super(cause);
  }
}
