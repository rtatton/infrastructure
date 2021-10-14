package org.cirrus.infrastructure.handler.topic;

import org.cirrus.infrastructure.handler.util.Logger;

public final class SubscribeQueueLoggerFactory {

  private static final SubscribeQueueComponent component = DaggerSubscribeQueueComponent.create();

  private SubscribeQueueLoggerFactory() {
    // No-op
  }

  public static Logger create() {
    return component.getLogger();
  }
}
