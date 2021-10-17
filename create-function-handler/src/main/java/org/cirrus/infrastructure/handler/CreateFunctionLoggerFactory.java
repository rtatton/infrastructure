package org.cirrus.infrastructure.handler;

import org.cirrus.infrastructure.util.Logger;

public final class CreateFunctionLoggerFactory {

  private static final CreateFunctionComponent component = DaggerCreateFunctionComponent.create();

  private CreateFunctionLoggerFactory() {
    // No-op
  }

  public static Logger create() {
    return component.getLogger();
  }
}
