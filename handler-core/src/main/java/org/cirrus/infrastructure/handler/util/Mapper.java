package org.cirrus.infrastructure.handler.util;

public interface Mapper {

  <T> T read(String content, Class<T> cls);

  String write(Object value);
}
