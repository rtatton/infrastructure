package org.cirrus.infrastructure.handler;

import org.cirrus.infrastructure.util.Logger;

public final class AddQueueLoggerFactory {

  private static final AddQueueComponent component = DaggerAddQueueComponent.create();

  private AddQueueLoggerFactory() {
    // No-op
  }

  public static Logger create() {
    return component.getLogger();
  }
}
