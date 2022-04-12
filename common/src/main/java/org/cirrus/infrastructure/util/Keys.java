package org.cirrus.infrastructure.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * This utility class serves as the ground truth for any configurable constants that require
 * cross-referencing in multiple modules.
 */
public final class Keys {
  // Node keys
  public static final String NODE_ID = "nodeId";
  public static final String FUNCTION_ID = "functionId";
  public static final String QUEUE_ID = "queueId";
  public static final String NODE_TABLE_NAME = "NodeRegistry";
  // Code keys
  public static final String CODE_ID = "codeId";
  public static final String ARTIFACT_ID = "artifactId";
  // Environment variables
  public static final String CODE_UPLOAD_BUCKET = "CODE_UPLOAD_BUCKET";
  public static final String NODE_BUCKET = "FUNCTION_BUCKET";
  public static final String NODE_KEY = "FUNCTION_KEY";
  public static final String NODE_HANDLER = "FUNCTION_HANDLER";
  public static final String NODE_RUNTIME = "FUNCTION_RUNTIME";
  public static final String NODE_ROLE = "FUNCTION_ROLE";
  // System properties
  private static final Properties properties = loadProperties();
  public static final String VERSION = getProperty("version");
  public static final String UPLOAD_HANDLER = getProperty("uploadHandler");
  public static final String PUBLISH_HANDLER = getProperty("publishHandler");
  public static final String CREATE_HANDLER = getProperty("createHandler");
  public static final String DELETE_HANDLER = getProperty("deleteHandler");

  private Keys() {
    // No-op
  }

  private static String getProperty(String key) {
    String property = properties.getProperty(key);
    if (property == null) {
      throw new NoSuchElementException("Property '" + key + "'" + " could not be found");
    }
    return property;
  }

  private static Properties loadProperties() {
    try {
      Properties properties = new Properties();
      properties.load(Files.newBufferedReader(Path.of("../gradle.properties")));
      return properties;
    } catch (IOException exception) {
      throw new UncheckedIOException(exception);
    }
  }
}
