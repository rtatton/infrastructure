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
  // Handler modules, packages, and classes -- KEEP MODULES IN SYNC WITH settings.gradle
  public static final String CREATE_HANDLER_MODULE = "create-node-handler";
  public static final String DELETE_HANDLER_MODULE = "delete-node-handler";
  public static final String PUBLISH_HANDLER_MODULE = "publish-code-handler";
  public static final String UPLOAD_HANDLER_MODULE = "upload-code-handler";
  public static final String CREATE_HANDLER_NAME = "CreateNodeHandler";
  public static final String DELETE_HANDLER_NAME = "DeleteNodeHandler";
  public static final String PUBLISH_HANDLER_NAME = "PublishCodeHandler";
  public static final String UPLOAD_HANDLER_NAME = "UploadCodeHandler";
  private static final String HANDLER_PACKAGE = "org.cirrus.infrastructure.handler.";
  public static final String CREATE_HANDLER_PATH = HANDLER_PACKAGE + CREATE_HANDLER_NAME;
  public static final String DELETE_HANDLER_PATH = HANDLER_PACKAGE + DELETE_HANDLER_NAME;
  public static final String PUBLISH_HANDLER_PATH = HANDLER_PACKAGE + PUBLISH_HANDLER_NAME;
  public static final String UPLOAD_HANDLER_PATH = HANDLER_PACKAGE + UPLOAD_HANDLER_NAME;
  // System properties
  private static final Properties properties = loadProperties();
  public static final String VERSION = getProperty("version");

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
