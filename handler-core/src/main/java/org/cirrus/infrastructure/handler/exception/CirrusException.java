package org.cirrus.infrastructure.handler.exception;

import org.cirrus.infrastructure.util.Preconditions;

public class CirrusException extends RuntimeException {

  public CirrusException() {
    super();
  }

  public CirrusException(String message) {
    super(Preconditions.checkNotNullOrEmpty(message));
  }

  public CirrusException(String message, Throwable cause) {
    super(Preconditions.checkNotNullOrEmpty(message), Preconditions.checkNotNull(cause));
  }

  public CirrusException(Throwable cause) {
    super(Preconditions.checkNotNull(cause));
  }

  public static CirrusException cast(Throwable throwable) {
    return throwable instanceof CirrusException
        ? (CirrusException) throwable
        : new CirrusException(throwable);
  }

  @Override
  public String getMessage() {
    String message = super.getMessage();
    return message == null ? "" : message;
  }
}
