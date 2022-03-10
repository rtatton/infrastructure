package org.cirrus.infrastructure.factory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.cirrus.infrastructure.util.Keys;
import org.immutables.builder.Builder;
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

final class ApiHandlerFactory {

  private static final int SUCCESS_EXIT_VALUE = 0;
  private static final String BASH = "bash";
  private static final String OPTION_C = "-c";
  private static final String CD_THEN_BUILD_THEN_COPY_FORMAT =
      "cd ./%1$s && ./gradlew build && cp build/distributions/%1$s.zip %2$s";
  private static final String HANDLER_PACKAGE_FORMAT = "org.cirrus.infrastructure.handler.%s";

  private ApiHandlerFactory() {
    // no-op
  }

  @Builder.Factory
  public static IFunction publishCodeHandler(
      @Builder.Parameter Construct scope,
      String apiHandler,
      String codePath,
      String region,
      String accessKeyId,
      String secretAccessKey) {
    return apiHandlerBuilder(scope, apiHandler, codePath)
        .environment(environment(region, accessKeyId, secretAccessKey))
        .build();
  }

  @Builder.Factory
  public static IFunction createNodeHandler(
      @Builder.Parameter Construct scope,
      String apiHandler,
      String codePath,
      String region,
      String accessKeyId,
      String secretAccessKey,
      String nodeRole,
      String nodeHandler,
      String nodeRuntime,
      String nodeRuntimeBucket,
      String nodeRuntimeKey) {
    Map<String, String> environment = environment(region, accessKeyId, secretAccessKey);
    environment.put(Keys.NODE_FUNCTION_ROLE, nodeRole);
    environment.put(Keys.NODE_FUNCTION_HANDLER, nodeHandler);
    environment.put(Keys.NODE_FUNCTION_RUNTIME, nodeRuntime);
    environment.put(Keys.NODE_FUNCTION_BUCKET, nodeRuntimeBucket);
    environment.put(Keys.NODE_FUNCTION_KEY, nodeRuntimeKey);
    return apiHandlerBuilder(scope, apiHandler, codePath).environment(environment).build();
  }

  @Builder.Factory
  public static IFunction deleteNodeHandler(
      @Builder.Parameter Construct scope,
      String apiHandler,
      String codePath,
      String region,
      String accessKeyId,
      String secretAccessKey) {
    return apiHandlerBuilder(scope, apiHandler, codePath)
        .environment(environment(region, accessKeyId, secretAccessKey))
        .build();
  }

  @Builder.Factory
  public static IFunction uploadCodeHandler(
      @Builder.Parameter Construct scope,
      String apiHandler,
      String codePath,
      String region,
      String accessKeyId,
      String secretAccessKey) {
    return apiHandlerBuilder(scope, apiHandler, codePath)
        .environment(environment(region, accessKeyId, secretAccessKey))
        .build();
  }

  private static Map<String, String> environment(
      String region, String accessKeyId, String secretAccessKey) {
    Map<String, String> environment = new HashMap<>();
    environment.put(Keys.AWS_REGION, region);
    environment.put(Keys.AWS_ACCESS_KEY_ID, accessKeyId);
    environment.put(Keys.AWS_SECRET_ACCESS_KEY, secretAccessKey);
    return environment;
  }

  /**
   * @param scope CDK construct scope.
   * @param handlerName Name of the Lambda function handler class.
   * @param codePath Relative path (from root, contains cdk.json) to the directory that contains the
   *     build files and source code for the Lambda function.
   * @return CDK Lambda function construct builder.
   */
  private static Function.Builder apiHandlerBuilder(
      Construct scope, String handlerName, String codePath) {
    return Function.Builder.create(scope, handlerName)
        .code(Code.fromAsset(codePath, assetOptions(codePath)))
        .runtime(Runtime.JAVA_11)
        .deadLetterQueueEnabled(true)
        .handler(formatName(handlerName))
        .timeout(Duration.seconds(60))
        .memorySize(128)
        .logRetention(RetentionDays.ONE_WEEK);
  }

  private static String formatName(String handlerName) {
    return String.format(HANDLER_PACKAGE_FORMAT, handlerName);
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

  private static String cdThenBuildThenCp(String codePath, String outputPath) {
    return String.format(CD_THEN_BUILD_THEN_COPY_FORMAT, codePath, outputPath);
  }
}
