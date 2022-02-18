package org.cirrus.infrastructure.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UncheckedIOException;
import javax.inject.Inject;

public final class JacksonMapper implements Mapper {

  private final ObjectMapper mapper;

  @Inject
  public JacksonMapper(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  public <T> T read(String content, Class<T> cls, Logger logger) {
    try {
      return mapper.readValue(content, cls);
    } catch (JsonProcessingException exception) {
      logger.error(exception.getLocalizedMessage());
      throw new UncheckedIOException(exception);
    }
  }

  public String write(Object value, Logger logger) {
    try {
      return mapper.writeValueAsString(value);
    } catch (JsonProcessingException exception) {
      logger.error(exception.getLocalizedMessage());
      throw new UncheckedIOException(exception);
    }
  }
}
