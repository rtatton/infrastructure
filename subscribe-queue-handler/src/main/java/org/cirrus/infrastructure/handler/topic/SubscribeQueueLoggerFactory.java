package org.cirrus.infrastructure.handler.topic;

import org.cirrus.infrastructure.handler.util.Logger;

final class SubscribeQueueLoggerFactory {

  private static final SubscribeQueueComponent component = DaggerSubscribeQueueComponent.create();

  public static Logger create() {
    return component.getLogger();
  }
}
