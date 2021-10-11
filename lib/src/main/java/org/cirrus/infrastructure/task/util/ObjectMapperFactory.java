package org.cirrus.infrastructure.task.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class ObjectMapperFactory {

  public static ObjectMapper create() {
    return new ObjectMapper();
  }
}
