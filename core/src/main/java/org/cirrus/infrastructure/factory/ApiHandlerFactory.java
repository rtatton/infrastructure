package org.cirrus.infrastructure.factory;

import java.io.IOException;
import software.amazon.awscdk.AssetHashType;
import software.amazon.awscdk.BundlingOptions;
import software.amazon.awscdk.BundlingOutput;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.IFunction;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.s3.assets.AssetOptions;
import software.constructs.Construct;

public final class ApiHandlerFactory {

  private static final int SUCCESS_EXIT_VALUE = 0;
  private static final String BASH = "bash";
  private static final String OPTION_C = "-c";
  private static final String CD_THEN_BUILD_THEN_COPY_FORMAT =
      "cd ./%1$s && ./gradlew build && cp build/distributions/%1$s.zip %2$s";
  private static final String HANDLER_PACKAGE_FORMAT = "org.cirrus.infrastructure.handler.%s";

  private ApiHandlerFactory() {
    // no-op
  }

  /**
   * @param functionName Name of the Lambda function handler class.
   * @param codePath Relative path (from root, contains cdk.json) to the directory that contains the
   *     build files and source code for the Lambda function.
   * @param scope CDK construct scope.
   * @return CDK Lambda function construct.
   */
  public static IFunction create(String functionName, String codePath, Construct scope) {
    return Function.Builder.create(scope, functionName)
        .code(Code.fromAsset(codePath, assetOptions(codePath)))
        .runtime(Runtime.JAVA_11)
        .role(null) // TODO
        .deadLetterQueueEnabled(true)
        .handler(handler(functionName))
        .timeout(Duration.seconds(60))
        .memorySize(128)
        .logRetention(RetentionDays.ONE_WEEK)
        .build();
  }

  private static String cdThenBuildThenCp(String codePath, String outputPath) {
    return String.format(CD_THEN_BUILD_THEN_COPY_FORMAT, codePath, outputPath);
  }

  private static String handler(String functionName) {
    return String.format(HANDLER_PACKAGE_FORMAT, functionName);
  }

  private static AssetOptions assetOptions(String codePath) {
    return AssetOptions.builder()
        .assetHashType(AssetHashType.OUTPUT)
        .bundling(bundlingOptions(codePath))
        .build();
  }

  private static BundlingOptions bundlingOptions(String codePath) {
    return BundlingOptions.builder()
        .local((outputPath, bundlingOptions) -> tryBundle(codePath, outputPath))
        .outputType(BundlingOutput.ARCHIVED)
        .build();
  }

  /**
   * Reference:
   * https://github.com/aws-samples/i-love-my-local-farmer/blob/main/DeliveryApi/cdk/src/main/java/com/ilmlf/delivery/api/ApiStack.java
   */
  private static boolean tryBundle(String codePath, String outputPath) {
    try {
      return processBuilder(codePath, outputPath).start().waitFor() == SUCCESS_EXIT_VALUE;
    } catch (IOException | InterruptedException exception) {
      exception.printStackTrace();
      return false;
    }
  }

  private static ProcessBuilder processBuilder(String codePath, String outputPath) {
    return new ProcessBuilder(BASH, OPTION_C, cdThenBuildThenCp(codePath, outputPath));
  }
}
