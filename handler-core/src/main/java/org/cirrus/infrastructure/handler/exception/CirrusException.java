package org.cirrus.infrastructure.handler.exception;

public class CirrusException extends RuntimeException {

  public CirrusException() {
    super();
  }

  public CirrusException(String message) {
    super(message);
  }

  public CirrusException(String message, Throwable cause) {
    super(message, cause);
  }

  public CirrusException(Throwable cause) {
    super(cause);
  }
}
