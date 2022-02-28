package org.cirrus.infrastructure.util;

public interface Converter<T, U> {

  U forward(T value);

  T backward(U value);
}
