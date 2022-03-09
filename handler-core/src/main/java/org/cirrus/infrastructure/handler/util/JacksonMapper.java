package org.cirrus.infrastructure.handler.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import org.cirrus.infrastructure.handler.exception.FailedMappingException;

public class JacksonMapper implements Mapper {

  private final ObjectMapper mapper;
  private final Logger logger;

  @Inject
  public JacksonMapper(ObjectMapper mapper, Logger logger) {
    this.mapper = mapper;
    this.logger = logger;
  }

  public <T> T read(String content, Class<T> cls) {
    try {
      return mapper.readValue(content, cls);
    } catch (JsonProcessingException exception) {
      logger.error(exception.getLocalizedMessage());
      throw new FailedMappingException(exception);
    }
  }

  public String write(Object value) {
    try {
      return mapper.writeValueAsString(value);
    } catch (JsonProcessingException exception) {
      logger.error(exception.getLocalizedMessage());
      throw new FailedMappingException(exception);
    }
  }
}
