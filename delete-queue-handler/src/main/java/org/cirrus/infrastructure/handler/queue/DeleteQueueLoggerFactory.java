package org.cirrus.infrastructure.handler.queue;

import org.cirrus.infrastructure.handler.util.Logger;

public final class DeleteQueueLoggerFactory {

  private static final DeleteQueueComponent component = DaggerDeleteQueueComponent.create();

  private DeleteQueueLoggerFactory() {
    // No-op
  }

  public static Logger create() {
    return component.getLogger();
  }
}
