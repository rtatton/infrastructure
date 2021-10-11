package org.cirrus.infrastructure.task.util;

@FunctionalInterface
public interface Command<T> {

  T run();
}
