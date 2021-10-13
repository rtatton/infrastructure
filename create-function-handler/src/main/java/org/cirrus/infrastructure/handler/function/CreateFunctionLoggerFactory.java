package org.cirrus.infrastructure.handler.function;

import org.cirrus.infrastructure.handler.util.Logger;

final class CreateFunctionLoggerFactory {

  private static final CreateFunctionComponent component = DaggerCreateFunctionComponent.create();

  public static Logger create() {
    return component.getLogger();
  }
}
