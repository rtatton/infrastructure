package org.cirrus.infrastructure.workflow;

import software.amazon.awscdk.services.stepfunctions.TaskStateBase;
import software.constructs.Construct;

public final class TopicStateFactory {

  private static final String CREATE_TOPIC = "CreateTopic";
  private static final String CREATE_TOPIC_PATH = ""; // TODO
  private static final String CREATE_TOPIC_COMMENT = "Creates an SNS topic";
  private static final String DELETE_TOPIC = "CreateTopic";
  private static final String DELETE_TOPIC_PATH = ""; // TODO
  private static final String DELETE_TOPIC_COMMENT = "Deletes an SNS topic";

  private TopicStateFactory() {}

  public static TopicStateFactory of() {
    return new TopicStateFactory();
  }

  public TaskStateBase createTopic(Construct scope) {
    return LambdaStateBuilder.create(scope)
        .setFunctionName(CREATE_TOPIC)
        .setCodePath(CREATE_TOPIC_PATH)
        .setComment(CREATE_TOPIC_COMMENT)
        .build();
  }

  public TaskStateBase deleteTopic(Construct scope) {
    return LambdaStateBuilder.create(scope)
        .setFunctionName(DELETE_TOPIC)
        .setCodePath(DELETE_TOPIC_PATH)
        .setComment(DELETE_TOPIC_COMMENT)
        .build();
  }
}
