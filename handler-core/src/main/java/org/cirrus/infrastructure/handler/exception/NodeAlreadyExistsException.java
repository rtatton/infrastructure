package org.cirrus.infrastructure.handler.exception;

public class NodeAlreadyExistsException extends RuntimeException {

  public NodeAlreadyExistsException() {}

  public NodeAlreadyExistsException(String message) {
    super(message);
  }

  public NodeAlreadyExistsException(String message, Throwable cause) {
    super(message, cause);
  }

  public NodeAlreadyExistsException(Throwable cause) {
    super(cause);
  }
}
