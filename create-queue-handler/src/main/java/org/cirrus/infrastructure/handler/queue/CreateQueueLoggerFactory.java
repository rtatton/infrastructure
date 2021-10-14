package org.cirrus.infrastructure.handler.queue;

import org.cirrus.infrastructure.util.Logger;

public final class CreateQueueLoggerFactory {

  private static final CreateQueueComponent component = DaggerCreateQueueComponent.create();

  private CreateQueueLoggerFactory() {
    // No-op
  }

  public static Logger create() {
    return component.getLogger();
  }
}
