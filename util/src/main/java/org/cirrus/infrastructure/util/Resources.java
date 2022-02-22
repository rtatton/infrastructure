package org.cirrus.infrastructure.util;

import java.util.UUID;

public enum Resources {
  FUNCTION,
  QUEUE,
  TOPIC;

  public static String createRandomId() {
    return UUID.randomUUID().toString();
  }
}
