package org.cirrus.infrastructure.handler.function;

import org.cirrus.infrastructure.handler.util.Logger;

public final class DeleteFunctionLoggerFactory {

  private static final DeleteFunctionComponent component = DaggerDeleteFunctionComponent.create();

  private DeleteFunctionLoggerFactory() {
    // No-op
  }

  public static Logger create() {
    return component.getLogger();
  }
}
