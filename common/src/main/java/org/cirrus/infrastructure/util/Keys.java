package org.cirrus.infrastructure.util;

/** This utility class serves as the ground truth for JSON keys and paths. */
public final class Keys {

  public static final String NODE_ID = "nodeId";
  public static final String FUNCTION_ID = "functionId";
  public static final String QUEUE_ID = "queueId";

  public static final String NODE_TABLE_NAME = "NodeRegistry";
  public static final String FUNCTION_CONFIG = "functionConfig";
  public static final String QUEUE_CONFIG = "queueConfig";

  public static final String UPLOAD_URL = "uploadUrl";
  public static final String CODE_BUCKET = "codeBucket";
  public static final String CODE_BUCKET_NAME = "3a5588b6-afd2-4df0-95e7-c1317027ac4c";
  public static final String CODE_KEY = "codeKey";

  public static final String CODE_URI = "codeUri";

  public static final String FUNCTION_HANDLER_NAME = "handlerName";
  public static final String FUNCTION_RUNTIME = "runtime";
  public static final String FUNCTION_MEMORY_SIZE = "memorySizeMegabytes";
  public static final String FUNCTION_TIMEOUT = "timeoutSeconds";
  public static final String FUNCTION_ENVIRONMENT = "environment";

  public static final String QUEUE_MESSAGE_RETENTION_PERIOD = "messageRetentionPeriodSeconds";
  public static final String QUEUE_DELAY_SECONDS = "delaySeconds";
  public static final String QUEUE_MAX_MESSAGE_SIZE = "maxMessageSizeBytes";
  public static final String QUEUE_RECEIVE_MESSAGE_WAIT_TIME = "receiveMessageWaitTimeSeconds";
  public static final String QUEUE_VISIBILITY_TIMEOUT = "visibilityTimeoutSeconds";
  public static final String QUEUE_BATCH_SIZE = "batchSize";

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
