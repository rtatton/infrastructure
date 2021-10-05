package org.cirrus.infrastructure.workflow;

import software.amazon.awscdk.services.stepfunctions.TaskStateBase;
import software.constructs.Construct;

public final class QueueStateFactory {

  private static final String CREATE_QUEUE = "CreateQueue"; // TODO
  private static final String CREATE_QUEUE_PATH = ""; // TODO
  private static final String CREATE_QUEUE_COMMENT = "Creates an SQS queue";
  private static final String DELETE_QUEUE = "CreateQueue"; // TODO
  private static final String DELETE_QUEUE_PATH = ""; // TODO
  private static final String DELETE_QUEUE_COMMENT = "Deletes an SQS queue";
  private final Construct scope;

  private QueueStateFactory(Construct scope) {
    this.scope = scope;
  }

  public static QueueStateFactory of(Construct scope) {
    return new QueueStateFactory(scope);
  }

  public TaskStateBase createQueue() {
    return LambdaStateBuilder.create(scope)
        .setFunctionName(CREATE_QUEUE)
        .setCodePath(CREATE_QUEUE_PATH)
        .setComment(CREATE_QUEUE_COMMENT)
        .build();
  }

  public TaskStateBase deleteQueue() {
    return LambdaStateBuilder.create(scope)
        .setFunctionName(DELETE_QUEUE)
        .setCodePath(DELETE_QUEUE_PATH)
        .setComment(DELETE_QUEUE_COMMENT)
        .build();
  }
}
