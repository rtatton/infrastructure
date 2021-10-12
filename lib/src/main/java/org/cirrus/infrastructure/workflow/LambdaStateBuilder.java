package org.cirrus.infrastructure.workflow;

import com.google.common.base.Preconditions;
import java.util.List;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.IFunction;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.stepfunctions.TaskStateBase;
import software.amazon.awscdk.services.stepfunctions.tasks.LambdaInvoke;
import software.constructs.Construct;

public final class LambdaStateBuilder {

  private static final Runtime RUNTIME = Runtime.JAVA_11;
  private static final Duration TIMEOUT = Duration.seconds(3);
  private final Construct scope;
  private String functionName;
  private String codePath;
  private String comment;

  private LambdaStateBuilder(Construct scope) {
    this.scope = scope;
  }

  public static LambdaStateBuilder create(Construct scope) {
    return new LambdaStateBuilder(scope);
  }

  public TaskStateBase build() {
    checkFields();
    return createState();
  }

  private void checkFields() {
    Preconditions.checkNotNull(codePath);
  }

  private TaskStateBase createState() {
    return LambdaInvoke.Builder.create(scope, functionName)
        .lambdaFunction(createFunction())
        .retryOnServiceExceptions(true)
        .comment(comment)
        .build();
  }

  private IFunction createFunction() {
    return Function.Builder.create(scope, functionName)
        .code(Code.fromDockerBuild(codePath))
        .architectures(List.of(Architecture.ARM_64))
        .runtime(RUNTIME)
        .timeout(TIMEOUT)
        .role(Role.fromRoleArn(scope, "", "")) // TODO
        .deadLetterQueueEnabled(true)
        .logRetention(RetentionDays.ONE_WEEK)
        .profiling(true)
        .build();
  }

  public LambdaStateBuilder setFunctionName(String functionName) {
    this.functionName = functionName;
    return this;
  }

  public LambdaStateBuilder setCodePath(String codePath) {
    this.codePath = codePath;
    return this;
  }

  public LambdaStateBuilder setComment(String comment) {
    this.comment = comment;
    return this;
  }
}
