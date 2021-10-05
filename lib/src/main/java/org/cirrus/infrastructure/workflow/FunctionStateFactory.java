package org.cirrus.infrastructure.workflow;

import software.amazon.awscdk.services.stepfunctions.TaskStateBase;
import software.constructs.Construct;

public final class FunctionStateFactory {

  private static final String CREATE_FUNCTION = "CreateFunction";
  private static final String CREATE_FUNCTION_PATH = ""; // TODO
  private static final String CREATE_FUNCTION_COMMENT = "Creates a function";
  private static final String DELETE_FUNCTION = "DeleteFunction";
  private static final String DELETE_FUNCTION_PATH = ""; // TODO
  private static final String DELETE_FUNCTION_COMMENT = "Deletes a function";

  private FunctionStateFactory() {}

  public static FunctionStateFactory of() {
    return new FunctionStateFactory();
  }

  public TaskStateBase createFunction(Construct scope) {
    return LambdaStateBuilder.create(scope)
        .setFunctionName(CREATE_FUNCTION)
        .setCodePath(CREATE_FUNCTION_PATH)
        .setComment(CREATE_FUNCTION_COMMENT)
        .build();
  }

  public TaskStateBase deleteFunction(Construct scope) {
    return LambdaStateBuilder.create(scope)
        .setFunctionName(DELETE_FUNCTION)
        .setCodePath(DELETE_FUNCTION_PATH)
        .setComment(DELETE_FUNCTION_COMMENT)
        .build();
  }
}
