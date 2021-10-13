package org.cirrus.infrastructure.handler.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class DefaultMapper implements Mapper {

  private final ObjectMapper mapper;

  private DefaultMapper() {
    this.mapper = new ObjectMapper();
  }

  public static DefaultMapper create() {
    return new DefaultMapper();
  }

  public <T> T read(String content, Class<T> cls, Logger logger) {
    try {
      return mapper.readValue(content, cls);
    } catch (JsonProcessingException exception) {
      logger.error(exception.getLocalizedMessage());
      throw new ObjectMappingException();
    }
  }

  public String write(Object value, Logger logger) {
    try {
      return mapper.writeValueAsString(value);
    } catch (JsonProcessingException exception) {
      logger.error(exception.getLocalizedMessage());
      throw new ObjectMappingException();
    }
  }
}
