package org.cirrus.infrastructure.util;

/** This utility class serves as the ground truth for JSON keys and paths. */
public final class Keys {
  // Node keys
  public static final String NODE_ID = "nodeId";
  public static final String FUNCTION_ID = "functionId";
  public static final String QUEUE_ID = "queueId";
  public static final String NODE_TABLE_NAME = "NodeRegistry";
  public static final String FUNCTION_CONFIG = "functionConfig";
  public static final String QUEUE_CONFIG = "queueConfig";
  // Code keys
  public static final String CODE_UPLOAD_URL = "uploadUrl";
  public static final String CODE_UPLOAD_BUCKET = "3a5588b6-afd2-4df0-95e7-c1317027ac4c";
  public static final String RUNTIME_BUCKET = "337f82a0-b3cc-4a23-8830-b464b6fd02cb";
  public static final String CODE_ID = "codeId";
  public static final String ARTIFACT_ID = "artifactId";
  public static final String NODE_HANDLER = "runtime.handle_request";
  public static final String NODE_RUNTIME = "python3.8";
  public static final String NODE_RUNTIME_KEY = "aries-cloudagent-python";
  // Function keys
  public static final String FUNCTION_RUNTIME = "runtime";
  public static final String FUNCTION_MEMORY_SIZE = "memorySizeMegabytes";
  public static final String FUNCTION_TIMEOUT = "timeoutSeconds";
  public static final String FUNCTION_ENVIRONMENT = "environment";
  // Queue keys
  public static final String QUEUE_MESSAGE_RETENTION_PERIOD = "messageRetentionPeriodSeconds";
  public static final String QUEUE_DELAY_SECONDS = "delaySeconds";
  public static final String QUEUE_MAX_MESSAGE_SIZE = "maxMessageSizeBytes";
  public static final String QUEUE_RECEIVE_MESSAGE_WAIT_TIME = "receiveMessageWaitTimeSeconds";
  public static final String QUEUE_VISIBILITY_TIMEOUT = "visibilityTimeoutSeconds";
  public static final String QUEUE_BATCH_SIZE = "batchSize";
  // Environment variables
  public static final String NODE_FUNCTION_BUCKET = "NODE_FUNCTION_BUCKET";
  public static final String NODE_FUNCTION_KEY = "NODE_FUNCTION_KEY";
  public static final String NODE_FUNCTION_HANDLER = "NODE_FUNCTION_HANDLER";
  public static final String NODE_FUNCTION_RUNTIME = "NODE_FUNCTION_RUNTIME";
  public static final String NODE_FUNCTION_ROLE = "NODE_FUNCTION_ROLE";
  public static final String AWS_REGION = "AWS_REGION";
  // DO NOT CHANGE VALUE; MUST BE 'AWS_ACCESS_KEY_ID'
  public static final String AWS_ACCESS_KEY_ID = "AWS_ACCESS_KEY_ID";
  // DO NOT CHANGE VALUE; MUST BE 'AWS_SECRET_ACCESS_KEY'
  public static final String AWS_SECRET_ACCESS_KEY = "AWS_SECRET_ACCESS_KEY";

  private Keys() {
    // No-op
  }
}
