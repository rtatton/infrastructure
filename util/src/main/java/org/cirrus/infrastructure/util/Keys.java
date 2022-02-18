package org.cirrus.infrastructure.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** This utility class serves as the ground truth for JSON keys and paths. */
public final class Keys {

  public static final String NODE_KEY = "nodeId";
  public static final String FUNCTION_KEY = "functionId";
  public static final String QUEUE_KEY = "queueId";
  public static final String TOPIC_KEY = "topicId";
  public static final String NODE_INPUT_KEY = "name";
  public static final String RESOURCE_KEY = "resourceId";
  public static final String NODE_TABLE_NAME = "NodeRegistry";
  public static final String FUNCTION_CONFIG_KEY = "functionConfig";
  public static final String QUEUE_CONFIG_KEY = "queueConfig";
  private static final int NODE_INDEX = 0;
  private static final int FUNCTION_INDEX = 0;
  private static final int QUEUE_INDEX = 1;
  private static final int TOPIC_INDEX = 2;
  private static final String KEY_PATH_FORMAT = "$[%d].%s";
  public static final String NODE_KEY_PATH = getNodeInputKey();
  public static final String FUNCTION_KEY_PATH = getFunctionKeyPath();
  public static final String QUEUE_KEY_PATH = getQueueKeyPath();
  public static final String TOPIC_KEY_PATH = getTopicKeyPath();

  private Keys() {
    // No-op
  }

  public static <T> List<T> sort(Map<Resource, T> map) {
    List<T> ordered = new ArrayList<>(3);
    addIfPresent(ordered, map, Resource.FUNCTION, FUNCTION_INDEX);
    addIfPresent(ordered, map, Resource.QUEUE, QUEUE_INDEX);
    addIfPresent(ordered, map, Resource.TOPIC, TOPIC_INDEX);
    return ordered;
  }

  private static <T> void addIfPresent(
      List<T> ordered, Map<Resource, T> map, Resource resource, int index) {
    if (map.containsKey(resource)) {
      ordered.add(index, map.get(resource));
    }
  }

  private static String getFunctionKeyPath() {
    return getResourceKeyPath(FUNCTION_INDEX);
  }

  private static String getResourceKeyPath(int index) {
    return String.format(KEY_PATH_FORMAT, index, RESOURCE_KEY);
  }

  private static String getQueueKeyPath() {
    return getResourceKeyPath(QUEUE_INDEX);
  }

  private static String getTopicKeyPath() {
    return getResourceKeyPath(TOPIC_INDEX);
  }

  private static String getNodeInputKey() {
    return String.format(KEY_PATH_FORMAT, NODE_INDEX, NODE_INPUT_KEY);
  }
}
