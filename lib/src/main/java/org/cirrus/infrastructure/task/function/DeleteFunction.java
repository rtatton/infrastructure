package org.cirrus.infrastructure.task.function;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import org.cirrus.infrastructure.task.util.Command;
import org.cirrus.infrastructure.task.util.ResourceUtil;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.DeleteFunctionRequest;
import software.amazon.awssdk.services.lambda.model.DeleteFunctionResponse;

public final class DeleteFunction implements Command<Void> {

  private static final DeleteFunctionFactory factory =
      DaggerFunctionComponent.create().newDeleteFunctionFactory();
  private final LambdaAsyncClient receiver;
  private final Function<String, DeleteFunctionRequest> requester;
  private final Consumer<Throwable> logger;
  private final String functionId;

  @AssistedInject
  DeleteFunction(
      LambdaAsyncClient receiver,
      Function<String, DeleteFunctionRequest> requester,
      Consumer<Throwable> logger,
      @Assisted String functionId) {
    this.receiver = receiver;
    this.requester = requester;
    this.logger = logger;
    this.functionId = functionId;
  }

  public static Command<Void> create(String functionId) {
    return factory.create(functionId);
  }

  @Override
  public Void run() {
    DeleteFunctionRequest request = requester.apply(functionId);
    Future<DeleteFunctionResponse> response = receiver.deleteFunction(request);
    ResourceUtil.logIfError(response, logger);
    return null;
  }
}
