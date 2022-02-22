package org.cirrus.infrastructure.handler;

public interface Command<I, O> {

  O run(I input);

  String runFromString(String input);
}
