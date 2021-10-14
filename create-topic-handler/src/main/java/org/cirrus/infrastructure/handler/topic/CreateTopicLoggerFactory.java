package org.cirrus.infrastructure.handler.topic;

import org.cirrus.infrastructure.util.Logger;

public final class CreateTopicLoggerFactory {

  private static final CreateTopicComponent component = DaggerCreateTopicComponent.create();

  public static Logger create() {
    return component.getLogger();
  }
}
