package org.cirrus.infrastructure.factory;

import software.amazon.awscdk.services.stepfunctions.TaskStateBase;
import software.constructs.Construct;

public final class FunctionStateFactory {

  private static final String CREATE_FUNCTION = "CreateFunction";
  private static final String CREATE_FUNCTION_DIR = "create-function-handler";
  private static final String CREATE_FUNCTION_COMMENT = "Creates a Lambda function";
  private static final String DELETE_FUNCTION = "DeleteFunction";
  private static final String DELETE_FUNCTION_DIR = "delete-function-handler";
  private static final String DELETE_FUNCTION_COMMENT = "Deletes a Lambda function";
  private static final String ADD_QUEUE = "AddQueue";
  private static final String ADD_QUEUE_DIR = "add-queue-handler";
  private static final String ADD_QUEUE_COMMENT =
      "Adds an SQS queue as an event-source mapping to a Lambda function";
  private final Construct scope;

  private FunctionStateFactory(Construct scope) {
    this.scope = scope;
  }

  public static FunctionStateFactory of(Construct scope) {
    return new FunctionStateFactory(scope);
  }

  public TaskStateBase newCreateFunctionState() {
    return LambdaStateBuilder.create(scope)
        .setFunctionName(CREATE_FUNCTION)
        .setCodeDirectoryName(CREATE_FUNCTION_DIR)
        .setComment(CREATE_FUNCTION_COMMENT)
        .build();
  }

  public TaskStateBase newDeleteFunctionState() {
    return LambdaStateBuilder.create(scope)
        .setFunctionName(DELETE_FUNCTION)
        .setCodeDirectoryName(DELETE_FUNCTION_DIR)
        .setComment(DELETE_FUNCTION_COMMENT)
        .build();
  }

  public TaskStateBase newAddQueueState() {
    return LambdaStateBuilder.create(scope)
        .setFunctionName(ADD_QUEUE)
        .setCodeDirectoryName(ADD_QUEUE_DIR)
        .setComment(ADD_QUEUE_COMMENT)
        .build();
  }
}
