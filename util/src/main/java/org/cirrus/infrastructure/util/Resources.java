package org.cirrus.infrastructure.util;

import java.util.UUID;

public final class Resources {

  public static String createRandomId() {
    return UUID.randomUUID().toString();
  }
}
