package org.cirrus.infrastructure.handler.function;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import java.util.concurrent.Future;
import java.util.function.Function;
import org.cirrus.infrastructure.util.Command;
import org.cirrus.infrastructure.util.Logger;
import org.cirrus.infrastructure.util.ResourceUtil;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.DeleteFunctionRequest;
import software.amazon.awssdk.services.lambda.model.DeleteFunctionResponse;

public final class DeleteFunctionCommand implements Command<Void> {

  private static final DeleteFunctionComponent component = DaggerDeleteFunctionComponent.create();
  private final LambdaAsyncClient receiver;
  private final Function<String, DeleteFunctionRequest> requester;
  private final Logger logger;
  private final String functionId;

  @AssistedInject
  DeleteFunctionCommand(
      LambdaAsyncClient receiver,
      Function<String, DeleteFunctionRequest> requester,
      Logger logger,
      @Assisted String functionId) {
    this.receiver = receiver;
    this.requester = requester;
    this.logger = logger;
    this.functionId = functionId;
  }

  public static Command<Void> create(String functionId) {
    return component.getDeleteFunctionCommandFactory().create(functionId);
  }

  @Override
  public Void run() {
    DeleteFunctionRequest request = requester.apply(functionId);
    Future<DeleteFunctionResponse> response = receiver.deleteFunction(request);
    ResourceUtil.logIfError(response, logger);
    return null;
  }
}
