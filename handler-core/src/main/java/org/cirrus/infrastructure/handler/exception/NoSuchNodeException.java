package org.cirrus.infrastructure.handler.exception;

public class NoSuchNodeException extends CirrusException {

  public NoSuchNodeException() {}

  public NoSuchNodeException(String message) {
    super(message);
  }

  public NoSuchNodeException(String message, Throwable cause) {
    super(message, cause);
  }

  public NoSuchNodeException(Throwable cause) {
    super(cause);
  }
}
