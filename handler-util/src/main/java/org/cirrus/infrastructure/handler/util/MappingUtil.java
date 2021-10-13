package org.cirrus.infrastructure.handler.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class MappingUtil {

  public static <T> T read(String content, Class<T> cls, Logger logger) {
    ObjectMapper mapper = create();
    try {
      return mapper.readValue(content, cls);
    } catch (JsonProcessingException exception) {
      logger.error(exception.getLocalizedMessage());
      throw new ObjectMappingException();
    }
  }

  public static ObjectMapper create() {
    return new ObjectMapper();
  }

  public static String write(Object value, Logger logger) {
    ObjectMapper mapper = create();
    try {
      return mapper.writeValueAsString(value);
    } catch (JsonProcessingException exception) {
      logger.error(exception.getLocalizedMessage());
      throw new ObjectMappingException();
    }
  }
}
