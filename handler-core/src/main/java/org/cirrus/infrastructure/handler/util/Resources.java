package org.cirrus.infrastructure.handler.util;

import java.util.UUID;

public final class Resources {

  public static String createRandomId() {
    return UUID.randomUUID().toString();
  }
}
