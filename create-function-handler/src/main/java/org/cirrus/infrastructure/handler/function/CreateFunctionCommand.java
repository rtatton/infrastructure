package org.cirrus.infrastructure.handler.function;

import java.util.concurrent.Future;
import java.util.function.Supplier;
import javax.inject.Inject;
import org.cirrus.infrastructure.handler.util.Command;
import org.cirrus.infrastructure.handler.util.Logger;
import org.cirrus.infrastructure.handler.util.ResourceUtil;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.CreateFunctionRequest;
import software.amazon.awssdk.services.lambda.model.CreateFunctionResponse;

public final class CreateFunctionCommand implements Command<String> {

  private static final CreateFunctionComponent component = DaggerCreateFunctionComponent.create();
  private final LambdaAsyncClient receiver;
  private final Supplier<CreateFunctionRequest> requester;
  private final Logger logger;

  @Inject
  CreateFunctionCommand(
      LambdaAsyncClient receiver, Supplier<CreateFunctionRequest> requester, Logger logger) {
    this.receiver = receiver;
    this.requester = requester;
    this.logger = logger;
  }

  public static Command<String> create() {
    return component.getCreateFunctionCommand();
  }

  @Override
  public String run() {
    CreateFunctionRequest request = requester.get();
    Future<CreateFunctionResponse> response = receiver.createFunction(request);
    return ResourceUtil.getOrNull(response, CreateFunctionResponse::functionName, logger);
  }
}
