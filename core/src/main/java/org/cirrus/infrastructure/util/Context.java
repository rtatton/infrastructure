package org.cirrus.infrastructure.util;

import software.amazon.awscdk.core.ConstructNode;

public class Context {

  private final ConstructNode node;

  private Context(ConstructNode node) {
    this.node = node;
  }

  public static Context of(ConstructNode node) {
    return new Context(node);
  }

  @SuppressWarnings("unchecked")
  public <T> T get(String key) {
    return (T) node.tryGetContext(key);
  }

  public void set(String key, Object value) {
    node.setContext(key, value);
  }
}
