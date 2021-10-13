package org.cirrus.infrastructure.handler.function;

import org.cirrus.infrastructure.handler.util.Logger;

final class DeleteFunctionLoggerFactory {

  private static final DeleteFunctionComponent component = DaggerDeleteFunctionComponent.create();

  public static Logger create() {
    return component.getLogger();
  }
}
