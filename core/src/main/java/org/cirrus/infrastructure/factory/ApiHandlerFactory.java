package org.cirrus.infrastructure.factory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cirrus.infrastructure.util.HandlerProps;
import org.cirrus.infrastructure.util.Keys;
import software.amazon.awscdk.core.AssetHashType;
import software.amazon.awscdk.core.BundlingOptions;
import software.amazon.awscdk.core.BundlingOutput;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.DockerVolume;
import software.amazon.awscdk.core.DockerVolumeConsistency;
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
  private static final String GRADLEW = RELATIVE_PATH_TO_ROOT + "gradlew";

  private ApiHandlerFactory() {
    // no-op
  }

  public static IFunction uploadCodeHandler(Construct scope, String codeUploadBucket) {
    return apiHandlerBuilder(scope, Keys.uploadHandlerProps())
        .environment(Map.of(Keys.CODE_UPLOAD_BUCKET, codeUploadBucket))
        .build();
  }

  public static IFunction publishCodeHandler(Construct scope) {
    return apiHandlerBuilder(scope, Keys.publishHandlerProps()).build();
  }

  public static IFunction createNodeHandler(
      Construct scope, String nodeRole, String nodeRuntimeBucket) {
    return apiHandlerBuilder(scope, Keys.createHandlerProps())
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
    return apiHandlerBuilder(scope, Keys.deleteHandlerProps()).build();
  }

  /** @param scope CDK construct scope. */
  private static Function.Builder apiHandlerBuilder(Construct scope, HandlerProps props) {
    return Function.Builder.create(scope, props.handlerName())
        .code(assetCode(props.handlerModule()))
        .runtime(Runtime.JAVA_11)
        .handler(props.handlerPath())
        .timeout(Duration.seconds(60))
        .memorySize(2048)
        .logRetention(RetentionDays.ONE_WEEK);
  }

  private static Code assetCode(String handlerModule) {
    return Code.fromAsset(pathToModule(handlerModule), assetOptions(handlerModule));
  }

  private static String pathToModule(String handlerModule) {
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
        .outputType(BundlingOutput.ARCHIVED)
        // Try to bundle locally first...
        .local((outputPath, bundlingOptions) -> tryBundle(handlerModule, outputPath))
        // and then try to bundle with Docker if local bundling fails.
        .command(dockerBuildUnsupported())
        .image(Runtime.JAVA_11.getBundlingImage())
        .user("root")
        .build();
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
        "(cd %s && %s && cp %s %s)",
        pathToModule(handlerModule), build(), distPath(handlerModule), outputPath);
  }

  private static List<String> dockerBuildUnsupported() {
    return List.of("/bin/sh", "-c", "echo 'Docker build not supported.'");
  }

  private static List<String> buildWithDocker(String handlerModule) {
    String buildThenCopy = build() + " && cp " + distPath(handlerModule) + " /asset-output/";
    return List.of("/bin/sh", "-c", buildThenCopy);
  }

  private static String build() {
    return GRADLEW + " clean check handlerZip";
  }

  // TODO(rtatton) Verify container path works before using buildWithDocker().
  private static DockerVolume volume() {
    return DockerVolume.builder()
        .consistency(DockerVolumeConsistency.DELEGATED)
        .hostPath(RELATIVE_PATH_TO_ROOT + System.getProperty("user.dir"))
        .containerPath("/root/root")
        .build();
  }

  private static String distPath(String handlerModule) {
    return "build/distributions/" + handlerModule + "-" + Keys.VERSION + ".zip";
  }
}
