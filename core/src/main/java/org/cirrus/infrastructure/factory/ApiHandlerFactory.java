package org.cirrus.infrastructure.factory;

import java.io.IOException;
import java.util.HashMap;
import org.cirrus.infrastructure.util.Keys;
import software.amazon.awscdk.core.AssetHashType;
import software.amazon.awscdk.core.BundlingOptions;
import software.amazon.awscdk.core.BundlingOutput;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.IFunction;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.s3.assets.AssetOptions;

public final class ApiHandlerFactory {

  public static final String NODE_HANDLER = "runtime.handle_request";
  public static final String NODE_RUNTIME = "python3.8";
  public static final String NODE_RUNTIME_KEY = "aries-cloudagent-python";
  private static final int SUCCESS_EXIT_VALUE = 0;
  private static final String BASH = "bash";
  private static final String OPTION_C = "-c";
  private static final String CD_THEN_BUILD_THEN_COPY_FORMAT =
      "cd ./%1$s && ./gradlew build && cp build/distributions/%1$s.zip %2$s";
  private static final String UPLOAD_CODE_HANDLER =
      "org.cirrus.infrastructure.handler.UploadCodeHandler";
  private static final String PUBLISH_CODE_HANDLER =
      "org.cirrus.infrastructure.handler.PublishCodeHandler";
  private static final String CREATE_NODE_HANDLER =
      "org.cirrus.infrastructure.handler.CreateNodeHandler";
  private static final String DELETE_NODE_HANDLER =
      "org.cirrus.infrastructure.handler.DeleteNodeHandler";
  private static final String UPLOAD_CODE_PATH = "../upload-code-handler";
  private static final String PUBLISH_CODE_PATH = "../publish-code-handler";
  private static final String CREATE_NODE_PATH = "../create-node-handler";
  private static final String DELETE_NODE_PATH = "../delete-node-handler";

  private ApiHandlerFactory() {
    // no-op
  }

  public static IFunction uploadCodeHandler(Construct scope) {
    return apiHandlerBuilder(scope, UPLOAD_CODE_HANDLER, UPLOAD_CODE_PATH).build();
  }

  public static IFunction publishCodeHandler(Construct scope) {
    return apiHandlerBuilder(scope, PUBLISH_CODE_HANDLER, PUBLISH_CODE_PATH).build();
  }

  public static IFunction createNodeHandler(
      Construct scope, String nodeRole, String nodeRuntimeBucket) {
    return apiHandlerBuilder(scope, CREATE_NODE_HANDLER, CREATE_NODE_PATH)
        .environment(
            new HashMap<>() {
              {
                put(Keys.NODE_FUNCTION_ROLE, nodeRole);
                put(Keys.NODE_FUNCTION_HANDLER, NODE_HANDLER);
                put(Keys.NODE_FUNCTION_RUNTIME, NODE_RUNTIME);
                put(Keys.NODE_FUNCTION_BUCKET, nodeRuntimeBucket);
                put(Keys.NODE_FUNCTION_KEY, NODE_RUNTIME_KEY);
              }
            })
        .build();
  }

  public static IFunction deleteNodeHandler(Construct scope) {
    return apiHandlerBuilder(scope, DELETE_NODE_HANDLER, DELETE_NODE_PATH).build();
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
        .handler(handlerName)
        .timeout(Duration.seconds(60))
        .memorySize(128)
        .logRetention(RetentionDays.ONE_WEEK);
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
        .image(null) // TODO
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
