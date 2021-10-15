package org.cirrus.infrastructure.resource.function;

import software.amazon.awscdk.services.stepfunctions.TaskStateBase;
import software.constructs.Construct;

public final class FunctionStateFactory {

  private static final String CREATE_FUNCTION = "CreateFunction";
  private static final String CREATE_FUNCTION_PATH = "./create-function-handler/";
  private static final String CREATE_FUNCTION_COMMENT = "Creates a Lambda function";
  private static final String DELETE_FUNCTION = "DeleteFunction";
  private static final String DELETE_FUNCTION_PATH = "./delete-function-handler/";
  private static final String DELETE_FUNCTION_COMMENT = "Deletes a Lambda function";
  private static final String ADD_QUEUE = "AddQueue";
  private static final String ADD_QUEUE_PATH = "./add-queue-handler/";
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
        .setCodeDirFromRoot(CREATE_FUNCTION_PATH)
        .setComment(CREATE_FUNCTION_COMMENT)
        .build();
  }

  public TaskStateBase newDeleteFunctionState() {
    return LambdaStateBuilder.create(scope)
        .setFunctionName(DELETE_FUNCTION)
        .setCodeDirFromRoot(DELETE_FUNCTION_PATH)
        .setComment(DELETE_FUNCTION_COMMENT)
        .build();
  }

  public TaskStateBase newAddQueueState() {
    return LambdaStateBuilder.create(scope)
        .setFunctionName(ADD_QUEUE)
        .setCodeDirFromRoot(ADD_QUEUE_PATH)
        .setComment(ADD_QUEUE_COMMENT)
        .build();
  }
}
