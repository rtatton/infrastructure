package org.cirrus.infrastructure.handler.function;

import org.cirrus.infrastructure.util.Logger;

public final class DeleteFunctionLoggerFactory {

  private static final DeleteFunctionComponent component = DaggerDeleteFunctionComponent.create();

  private DeleteFunctionLoggerFactory() {
    // No-op
  }

  public static Logger create() {
    return component.getLogger();
  }
}
