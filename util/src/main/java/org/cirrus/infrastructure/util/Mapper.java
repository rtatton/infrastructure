package org.cirrus.infrastructure.util;

public interface Mapper {

  static Mapper create() {
    return new JacksonMapper();
  }

  <T> T read(String content, Class<T> cls, Logger logger);

  String write(Object value, Logger logger);
}
