package org.cirrus.infrastructure.factory;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.List;
import software.amazon.awscdk.core.AssetHashType;
import software.amazon.awscdk.core.BundlingOptions;
import software.amazon.awscdk.core.BundlingOutput;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.IFunction;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.s3.assets.AssetOptions;
import software.amazon.awscdk.services.stepfunctions.TaskStateBase;
import software.amazon.awscdk.services.stepfunctions.tasks.LambdaInvoke;
import software.constructs.Construct;

public final class LambdaStateBuilder {

  private static final String USER = "root";
  private static final String SH = "/bin/sh";
  private static final String BASH = "bash";
  private static final String OPTION_C = "-c";
  private static final String CD_THEN_BUILD_THEN_COPY_FORMAT =
      "cd ./%1$s && ./gradlew build && cp build/distributions/%1$s.zip %2$s";
  private static final String BUILD_THEN_LS_THEN_COPY_FORMAT =
      "./gradlew build && ls /asset-output/ && cp build/distributions/%s.zip /asset-output/";
  private static final String HANDLER_PACKAGE_FORMAT = "org.cirrus.infrastructure.handler.%s";
  private static final Runtime RUNTIME = Runtime.JAVA_11;
  private static final Duration TIMEOUT = Duration.seconds(3);
  private final Construct scope;
  private String functionName;
  private String codeDirectoryName;
  private String comment;

  private LambdaStateBuilder(Construct scope) {
    this.scope = scope;
  }

  /** Creates a builder for instantiating Step Function tasks that invoke a Lambda function. */
  public static LambdaStateBuilder create(Construct scope) {
    return new LambdaStateBuilder(scope);
  }

  /** Creates a Step Function task that invokes a Lambda function. */
  public TaskStateBase build() {
    checkFields();
    return createState();
  }

  private void checkFields() {
    Preconditions.checkNotNull(functionName);
    Preconditions.checkNotNull(codeDirectoryName);
    Preconditions.checkNotNull(comment);
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
        .code(Code.fromAsset(codeDirectoryName, getAssetOptions()))
        .architectures(List.of(Architecture.ARM_64))
        .runtime(RUNTIME)
        .timeout(TIMEOUT)
        .role(Role.fromRoleArn(scope, "", "")) // TODO
        .deadLetterQueueEnabled(true)
        .handler(getHandler())
        .logRetention(RetentionDays.ONE_WEEK)
        .build();
  }

  private AssetOptions getAssetOptions() {
    return AssetOptions.builder()
        .assetHashType(AssetHashType.OUTPUT)
        .bundling(getBundlingOptions())
        .build();
  }

  private String getHandler() {
    return String.format(HANDLER_PACKAGE_FORMAT, functionName);
  }

  private BundlingOptions getBundlingOptions() {
    return BundlingOptions.builder()
        .local((outputPath, bundlingOptions) -> tryLocalBundling(outputPath))
        .command(getContainerCommand())
        .image(RUNTIME.getBundlingImage())
        .user(USER)
        .outputType(BundlingOutput.ARCHIVED)
        .build();
  }

  /**
   * Reference:
   * https://github.com/aws-samples/i-love-my-local-farmer/blob/main/DeliveryApi/cdk/src/main/java/com/ilmlf/delivery/api/ApiStack.java
   */
  private boolean tryLocalBundling(String outputPath) {
    try {
      // Paths relative to cdk.json in root directory
      ProcessBuilder builder = getBuilder(outputPath);
      Process process = builder.start();
      process.waitFor();
      return process.exitValue() == 0;
    } catch (IOException | InterruptedException exception) {
      exception.printStackTrace();
      return false;
    }
  }

  private List<String> getContainerCommand() {
    return List.of(SH, OPTION_C, getBuildThenLsThenCopyCommand());
  }

  private String getBuildThenLsThenCopyCommand() {
    return String.format(BUILD_THEN_LS_THEN_COPY_FORMAT, codeDirectoryName);
  }

  private ProcessBuilder getBuilder(String outputPath) {
    return new ProcessBuilder(BASH, OPTION_C, getCdThenBuildThenCopyCommand(outputPath));
  }

  private String getCdThenBuildThenCopyCommand(String outputPath) {
    return String.format(CD_THEN_BUILD_THEN_COPY_FORMAT, codeDirectoryName, outputPath);
  }

  /** Sets the name of the function that will be used as part of the logical ID of the construct. */
  public LambdaStateBuilder setFunctionName(String functionName) {
    this.functionName = functionName;
    return this;
  }

  /** Sets the name of the top-level directory that contains the build files and source code. */
  public LambdaStateBuilder setCodeDirectoryName(String codeDirectoryName) {
    this.codeDirectoryName = codeDirectoryName;
    return this;
  }

  /** Sets the comment that will be provided as part of the state in the Step Function. */
  public LambdaStateBuilder setComment(String comment) {
    this.comment = comment;
    return this;
  }
}