package org.cirrus.infrastructure.util;

public interface Mapper {

  <T> T read(String content, Class<T> cls);

  String write(Object value);
}
