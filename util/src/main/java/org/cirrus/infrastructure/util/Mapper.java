package org.cirrus.infrastructure.util;

public interface Mapper {

  <T> T read(String content, Class<T> cls, Logger logger);

  String write(Object value, Logger logger);
}
