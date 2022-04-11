package org.cirrus.infrastructure.util;

/** This utility class serves as the ground truth for JSON keys and paths. */
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

  private Keys() {
    // No-op
  }
}
