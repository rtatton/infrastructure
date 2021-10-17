package org.cirrus.infrastructure.handler;

import org.cirrus.infrastructure.util.Logger;

public final class DeleteTopicLoggerFactory {

  private static final DeleteTopicComponent component = DaggerDeleteTopicComponent.create();

  private DeleteTopicLoggerFactory() {
    // No-op
  }

  public static Logger create() {
    return component.getLogger();
  }
}
