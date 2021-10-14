package org.cirrus.infrastructure.util;

@FunctionalInterface
public interface Command<T> {

  T run();
}
