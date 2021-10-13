package org.cirrus.infrastructure.handler.util;

public interface Mapper {

  static Mapper create() {
    return DefaultMapper.create();
  }

  <T> T read(String content, Class<T> aClass, Logger logger);

  String write(Object value, Logger logger);
}
