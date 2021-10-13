package org.cirrus.infrastructure.handler.function;

import org.cirrus.infrastructure.handler.util.Logger;

final class AddQueueLoggerFactory {

  private static final AddQueueComponent component = DaggerAddQueueComponent.create();

  public static Logger create() {
    return component.getLogger();
  }
}
