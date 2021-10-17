package org.cirrus.infrastructure.handler;

import org.cirrus.infrastructure.util.Logger;

public final class SubscribeQueueLoggerFactory {

  private static final SubscribeQueueComponent component = DaggerSubscribeQueueComponent.create();

  private SubscribeQueueLoggerFactory() {
    // No-op
  }

  public static Logger create() {
    return component.getLogger();
  }
}
