package org.cirrus.infrastructure.factory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  private static final int SUCCESS_EXIT_VALUE = 0;
  private static final String RELATIVE_PATH_TO_ROOT = "../";
  private static final String UPLOAD_CODE_HANDLER =
      "org.cirrus.infrastructure.handler.UploadCodeHandler";
  private static final String PUBLISH_CODE_HANDLER =
      "org.cirrus.infrastructure.handler.PublishCodeHandler";
  private static final String CREATE_NODE_HANDLER =
      "org.cirrus.infrastructure.handler.CreateNodeHandler";
  private static final String DELETE_NODE_HANDLER =
      "org.cirrus.infrastructure.handler.DeleteNodeHandler";

  private ApiHandlerFactory() {
    // no-op
  }

  public static IFunction uploadCodeHandler(Construct scope, String codeUploadBucket) {
    return apiHandlerBuilder(scope, UPLOAD_CODE_HANDLER, Keys.UPLOAD_HANDLER)
        .environment(Map.of(Keys.CODE_UPLOAD_BUCKET, codeUploadBucket))
        .build();
  }

  public static IFunction publishCodeHandler(Construct scope) {
    return apiHandlerBuilder(scope, PUBLISH_CODE_HANDLER, Keys.PUBLISH_HANDLER).build();
  }

  public static IFunction createNodeHandler(
      Construct scope, String nodeRole, String nodeRuntimeBucket) {
    return apiHandlerBuilder(scope, CREATE_NODE_HANDLER, Keys.CREATE_HANDLER)
        .environment(
            new HashMap<>() {
              {
                put(Keys.NODE_ROLE, nodeRole);
                put(Keys.NODE_HANDLER, "runtime.handle_request");
                put(Keys.NODE_RUNTIME, "python3.8");
                put(Keys.NODE_BUCKET, nodeRuntimeBucket);
                put(Keys.NODE_KEY, "aries-cloudagent-python");
              }
            })
        .build();
  }

  public static IFunction deleteNodeHandler(Construct scope) {
    return apiHandlerBuilder(scope, DELETE_NODE_HANDLER, Keys.DELETE_HANDLER).build();
  }

  /**
   * @param scope CDK construct scope.
   * @param handlerName Fully-qualified name of the Lambda function handler class.
   * @param handlerModule Root directory containing the build files and source code for the handler.
   * @return CDK Lambda function construct builder.
   */
  private static Function.Builder apiHandlerBuilder(
      Construct scope, String handlerName, String handlerModule) {
    String modulePath = pathToHandlerModule(handlerModule);
    return Function.Builder.create(scope, handlerName)
        .code(Code.fromAsset(modulePath, assetOptions(handlerModule)))
        .runtime(Runtime.JAVA_11)
        .deadLetterQueueEnabled(true)
        .handler(handlerName)
        .timeout(Duration.seconds(60))
        .memorySize(128)
        .logRetention(RetentionDays.ONE_WEEK);
  }

  private static String pathToHandlerModule(String handlerModule) {
    return RELATIVE_PATH_TO_ROOT + handlerModule;
  }

  private static AssetOptions assetOptions(String handlerModule) {
    return AssetOptions.builder()
        .assetHashType(AssetHashType.OUTPUT)
        .bundling(bundlingOptions(handlerModule))
        .build();
  }

  private static BundlingOptions bundlingOptions(String handlerModule) {
    return BundlingOptions.builder()
        .local((outputPath, bundlingOptions) -> tryBundle(handlerModule, outputPath))
        .outputType(BundlingOutput.ARCHIVED)
        .image(Runtime.JAVA_11.getBundlingImage())
        .user("root")
        .command(buildWithDocker(handlerModule))
        .build();
  }

  private static List<String> buildWithDocker(String handlerModule) {
    String buildThenCopyOutput =
        String.format(
            "%s build && ls /asset-output/ && cp %s /asset-output/",
            gradlew(), distPath(handlerModule));
    return List.of("/bin/sh", "-c", buildThenCopyOutput);
  }

  private static boolean tryBundle(String handlerModule, String outputPath) {
    try {
      return processBuilder(handlerModule, outputPath).start().waitFor() == SUCCESS_EXIT_VALUE;
    } catch (IOException | InterruptedException exception) {
      exception.printStackTrace();
      return false;
    }
  }

  private static ProcessBuilder processBuilder(String handlerModule, String outputPath) {
    return new ProcessBuilder("bash", "-c", buildLocally(handlerModule, outputPath));
  }

  private static String buildLocally(String handlerModule, String outputPath) {
    return String.format(
        "cd %s && %s build && cp %s %s",
        pathToHandlerModule(handlerModule), gradlew(), distPath(handlerModule), outputPath);
  }

  private static String gradlew() {
    return RELATIVE_PATH_TO_ROOT + "gradlew";
  }

  private static String distPath(String handlerModule) {
    return "build/distributions/" + handlerModule + "-" + Keys.VERSION + ".zip";
  }
}
