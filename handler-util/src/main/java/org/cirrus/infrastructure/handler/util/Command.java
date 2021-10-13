package org.cirrus.infrastructure.handler.util;

@FunctionalInterface
public interface Command<T> {

  T run();
}
