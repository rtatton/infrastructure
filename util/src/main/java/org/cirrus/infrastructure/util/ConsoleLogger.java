package org.cirrus.infrastructure.util;

public class ConsoleLogger implements Logger {

  private final String name;

  public ConsoleLogger(String name) {
    this.name = name;
  }

  @Override
  public void debug(String message) {
    System.out.println(message);
  }

  @Override
  public void info(String message) {
    System.out.println(message);
  }

  @Override
  public void warn(String message) {
    System.err.println(message);
  }

  @Override
  public void error(String message) {
    System.err.println(message);
  }

  public String getName() {
    return name;
  }
}
