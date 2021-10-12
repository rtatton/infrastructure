package org.cirrus.infrastructure.task.function;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.inject.Inject;
import org.cirrus.infrastructure.task.Command;
import org.cirrus.infrastructure.task.util.ResourceUtil;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.CreateFunctionRequest;
import software.amazon.awssdk.services.lambda.model.CreateFunctionResponse;

public final class CreateFunction implements Command<String> {

  private static final FunctionComponent component = DaggerFunctionComponent.create();
  private final LambdaAsyncClient receiver;
  private final Supplier<CreateFunctionRequest> requester;
  private final Consumer<Throwable> logger;

  @Inject
  CreateFunction(
      LambdaAsyncClient receiver,
      Supplier<CreateFunctionRequest> requester,
      Consumer<Throwable> logger) {
    this.receiver = receiver;
    this.requester = requester;
    this.logger = logger;
  }

  public static Command<String> create() {
    return component.getCreateFunction();
  }

  @Override
  public String run() {
    CreateFunctionRequest request = requester.get();
    Future<CreateFunctionResponse> response = receiver.createFunction(request);
    return ResourceUtil.getIdOrNull(response, CreateFunctionResponse::functionName, logger);
  }
}
