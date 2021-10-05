package org.cirrus.infrastructure.workflow;

import software.amazon.awscdk.services.stepfunctions.TaskStateBase;
import software.constructs.Construct;

public final class FunctionStateFactory {

  private static final String CREATE_FUNCTION = "CreateFunction";
  private static final String CREATE_FUNCTION_PATH = ""; // TODO
  private static final String CREATE_FUNCTION_COMMENT = "Creates a Lambda function";
  private static final String DELETE_FUNCTION = "DeleteFunction";
  private static final String DELETE_FUNCTION_PATH = ""; // TODO
  private static final String DELETE_FUNCTION_COMMENT = "Deletes a Lambda function";
  private static final String ADD_QUEUE = "AddQueue";
  private static final String ADD_QUEUE_PATH = ""; // TODO
  private static final String ADD_QUEUE_COMMENT =
      "Adds an SQS queue as an event-source mapping to a Lambda function";
  private final Construct scope;

  private FunctionStateFactory(Construct scope) {
    this.scope = scope;
  }

  public static FunctionStateFactory of(Construct scope) {
    return new FunctionStateFactory(scope);
  }

  public TaskStateBase createFunction() {
    return LambdaStateBuilder.create(scope)
        .setFunctionName(CREATE_FUNCTION)
        .setCodePath(CREATE_FUNCTION_PATH)
        .setComment(CREATE_FUNCTION_COMMENT)
        .build();
  }

  public TaskStateBase deleteFunction() {
    return LambdaStateBuilder.create(scope)
        .setFunctionName(DELETE_FUNCTION)
        .setCodePath(DELETE_FUNCTION_PATH)
        .setComment(DELETE_FUNCTION_COMMENT)
        .build();
  }

  public TaskStateBase addQueue() {
    return LambdaStateBuilder.create(scope)
        .setFunctionName(ADD_QUEUE)
        .setCodePath(ADD_QUEUE_PATH)
        .setComment(ADD_QUEUE_COMMENT)
        .build();
  }
}
