package org.cirrus.infrastructure.factory;

import software.amazon.awscdk.services.stepfunctions.TaskStateBase;
import software.constructs.Construct;

public final class TopicStateFactory {

  private static final String CREATE_TOPIC = "CreateTopic";
  private static final String CREATE_TOPIC_PATH = "./create-topic-handler/";
  private static final String CREATE_TOPIC_COMMENT = "Creates an SNS topic";
  private static final String DELETE_TOPIC = "DeleteTopic";
  private static final String DELETE_TOPIC_PATH = "./delete-topic-handler/";
  private static final String DELETE_TOPIC_COMMENT = "Deletes an SNS topic";
  private static final String SUBSCRIBE_QUEUE = "SubscribeQueue";
  private static final String SUBSCRIBE_QUEUE_PATH = "./subscribe-queue-handler/";
  private static final String SUBSCRIBE_QUEUE_COMMENT = "Subscribes an SQS queue to an SNS topic";
  private final Construct scope;

  private TopicStateFactory(Construct scope) {
    this.scope = scope;
  }

  public static TopicStateFactory of(Construct scope) {
    return new TopicStateFactory(scope);
  }

  public TaskStateBase newCreateTopicState() {
    return LambdaStateBuilder.create(scope)
        .setFunctionName(CREATE_TOPIC)
        .setCodeDirectoryName(CREATE_TOPIC_PATH)
        .setComment(CREATE_TOPIC_COMMENT)
        .build();
  }

  public TaskStateBase newDeleteTopicState() {
    return LambdaStateBuilder.create(scope)
        .setFunctionName(DELETE_TOPIC)
        .setCodeDirectoryName(DELETE_TOPIC_PATH)
        .setComment(DELETE_TOPIC_COMMENT)
        .build();
  }

  public TaskStateBase newSubscribeQueueState() {
    return LambdaStateBuilder.create(scope)
        .setFunctionName(SUBSCRIBE_QUEUE)
        .setCodeDirectoryName(SUBSCRIBE_QUEUE_PATH)
        .setComment(SUBSCRIBE_QUEUE_COMMENT)
        .build();
  }
}
