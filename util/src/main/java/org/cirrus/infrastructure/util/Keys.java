package org.cirrus.infrastructure.util;

/** This utility class serves as the ground truth for JSON keys and paths. */
public final class Keys {

  public static final String NODE_KEY = "nodeId";
  public static final String FUNCTION_KEY = "functionId";
  public static final String QUEUE_KEY = "queueId";
  public static final String NODE_TABLE_NAME = "NodeRegistry";
  public static final String FUNCTION_CONFIG_KEY = "functionConfig";
  public static final String QUEUE_CONFIG_KEY = "queueConfig";

  private Keys() {
    // No-op
  }
}
