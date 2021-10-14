package org.cirrus.infrastructure.handler.function;

import dagger.Module;
import dagger.Provides;
import java.util.function.Function;
import javax.inject.Singleton;
import org.cirrus.infrastructure.handler.util.Logger;
import software.amazon.awssdk.services.lambda.model.DeleteFunctionRequest;

@Module
final class DeleteFunctionModule {

  private DeleteFunctionModule() {
    // No-op
  }

  @Provides
  @Singleton
  public static Function<String, DeleteFunctionRequest> provideDeleteRequester() {
    return functionId -> DeleteFunctionRequest.builder().functionName(functionId).build();
  }

  @Provides
  @Singleton
  public static Logger provideLogger() {
    return Logger.of("DeleteFunction");
  }
}
