package org.cirrus.infrastructure.task;

@FunctionalInterface
public interface Command<T> {

  T run();
}
