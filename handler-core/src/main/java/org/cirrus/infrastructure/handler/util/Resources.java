package org.cirrus.infrastructure.handler.util;

import java.util.UUID;

public final class Resources {

  private Resources() {
    // no-op
  }

  public static String createRandomId() {
    return UUID.randomUUID().toString();
  }
}
