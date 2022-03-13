package org.cirrus.infrastructure.handler.exception;

public class FailedCodeUploadException extends CirrusException {

  public FailedCodeUploadException() {}

  public FailedCodeUploadException(String message) {
    super(message);
  }

  public FailedCodeUploadException(String message, Throwable cause) {
    super(message, cause);
  }

  public FailedCodeUploadException(Throwable cause) {
    super(cause);
  }
}
