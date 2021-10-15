package org.cirrus.infrastructure.handler.function;

import dagger.Module;
import dagger.Provides;
import java.util.function.Supplier;
import javax.inject.Singleton;
import org.cirrus.infrastructure.util.Logger;
import org.cirrus.infrastructure.util.ResourceUtil;
import software.amazon.awssdk.services.lambda.model.CreateFunctionRequest;
import software.amazon.awssdk.services.lambda.model.FunctionCode;
import software.amazon.awssdk.services.lambda.model.PackageType;
import software.amazon.awssdk.services.lambda.model.Runtime;

@Module
final class CreateFunctionModule {

  private static final String HANDLER_NAME = ""; // TODO Move to API
  private static final String CODE_SIGNING_CONFIG_ARN = ""; // TODO Move to API
  private static final int MEMORY_SIZE_IN_MB = 256; // TODO Move to API
  private static final PackageType PACKAGE_TYPE = PackageType.IMAGE; // TODO Move to API
  private static final int TIMEOUT_IN_SECONDS = 3;
  private static final String FUNCTION_ROLE = "";
  private static final String IMAGE_URI = ""; // TODO Move to API
  private static final Runtime RUNTIME = Runtime.JAVA11; // TODO Move to API
  private static final FunctionCode CODE = FunctionCode.builder().imageUri(IMAGE_URI).build();
  private static final String LOGGER_NAME = "CreateFunction";

  private CreateFunctionModule() {
    // No-op
  }

  @Provides
  @Singleton
  public static Supplier<CreateFunctionRequest> provideCreateRequester() {
    return () ->
        CreateFunctionRequest.builder()
            .functionName(ResourceUtil.createRandomId())
            .packageType(PACKAGE_TYPE)
            .code(CODE)
            .runtime(RUNTIME)
            .handler(HANDLER_NAME)
            .codeSigningConfigArn(CODE_SIGNING_CONFIG_ARN)
            .memorySize(MEMORY_SIZE_IN_MB)
            .timeout(TIMEOUT_IN_SECONDS)
            .role(FUNCTION_ROLE)
            .publish(true)
            .build();
  }

  @Provides
  @Singleton
  public static Logger provideLogger() {
    return Logger.of(LOGGER_NAME);
  }
}
