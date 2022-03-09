package org.cirrus.infrastructure.handler.exception;

public class FailedCodePublicationException extends CirrusException {

  public FailedCodePublicationException() {}

  public FailedCodePublicationException(String message) {
    super(message);
  }

  public FailedCodePublicationException(String message, Throwable cause) {
    super(message, cause);
  }

  public FailedCodePublicationException(Throwable cause) {
    super(cause);
  }
}
