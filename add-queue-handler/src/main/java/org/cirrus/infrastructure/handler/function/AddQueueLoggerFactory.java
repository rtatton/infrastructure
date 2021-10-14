package org.cirrus.infrastructure.handler.function;

import org.cirrus.infrastructure.handler.util.Logger;

public final class AddQueueLoggerFactory {

  private static final AddQueueComponent component = DaggerAddQueueComponent.create();

  private AddQueueLoggerFactory() {
    // No-op
  }

  public static Logger create() {
    return component.getLogger();
  }
}
