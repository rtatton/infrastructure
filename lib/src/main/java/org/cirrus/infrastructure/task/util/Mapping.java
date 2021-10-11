package org.cirrus.infrastructure.task.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Consumer;
import org.cirrus.infrastructure.task.exception.ObjectMappingException;

public final class Mapping {

  public static <T> T read(String content, Class<T> cls, Consumer<Throwable> logger) {
    ObjectMapper mapper = create();
    try {
      return mapper.readValue(content, cls);
    } catch (JsonProcessingException exception) {
      logger.accept(exception);
      throw new ObjectMappingException();
    }
  }

  public static ObjectMapper create() {
    return new ObjectMapper();
  }

  public static String write(Object value, Consumer<Throwable> logger) {
    ObjectMapper mapper = create();
    try {
      return mapper.writeValueAsString(value);
    } catch (JsonProcessingException exception) {
      logger.accept(exception);
      throw new ObjectMappingException();
    }
  }
}
