package org.cirrus.infrastructure.factory;

import software.amazon.awscdk.services.stepfunctions.TaskStateBase;
import software.constructs.Construct;

public final class QueueStateFactory {

  private static final String CREATE_QUEUE = "CreateQueue";
  private static final String CREATE_QUEUE_PATH = "./create-queue-handler/";
  private static final String CREATE_QUEUE_COMMENT = "Creates an SQS queue";
  private static final String DELETE_QUEUE = "DeleteQueue";
  private static final String DELETE_QUEUE_PATH = "./delete-queue-handler/";
  private static final String DELETE_QUEUE_COMMENT = "Deletes an SQS queue";
  private final Construct scope;

  private QueueStateFactory(Construct scope) {
    this.scope = scope;
  }

  public static QueueStateFactory of(Construct scope) {
    return new QueueStateFactory(scope);
  }

  public TaskStateBase newCreateQueueState() {
    return LambdaStateBuilder.create(scope)
        .setFunctionName(CREATE_QUEUE)
        .setCodeDirectoryName(CREATE_QUEUE_PATH)
        .setComment(CREATE_QUEUE_COMMENT)
        .build();
  }

  public TaskStateBase newDeleteQueueState() {
    return LambdaStateBuilder.create(scope)
        .setFunctionName(DELETE_QUEUE)
        .setCodeDirectoryName(DELETE_QUEUE_PATH)
        .setComment(DELETE_QUEUE_COMMENT)
        .build();
  }
}
