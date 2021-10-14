package org.cirrus.infrastructure.handler.function;

import org.cirrus.infrastructure.handler.util.Logger;

public final class CreateFunctionLoggerFactory {

  private static final CreateFunctionComponent component = DaggerCreateFunctionComponent.create();

  private CreateFunctionLoggerFactory() {
    // No-op
  }

  public static Logger create() {
    return component.getLogger();
  }
}
