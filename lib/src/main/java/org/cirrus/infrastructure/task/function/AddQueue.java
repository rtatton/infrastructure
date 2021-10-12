package org.cirrus.infrastructure.task.function;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.cirrus.infrastructure.task.util.Command;
import org.cirrus.infrastructure.task.util.ResourceUtil;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.CreateEventSourceMappingRequest;
import software.amazon.awssdk.services.lambda.model.CreateEventSourceMappingResponse;

public final class AddQueue implements Command<Void> {
  private static final AddQueueFactory factory =
      DaggerFunctionComponent.create().newAddQueueFactory();
  private final LambdaAsyncClient receiver;
  private final BiFunction<String, String, CreateEventSourceMappingRequest> requester;
  private final Consumer<Throwable> logger;
  private final String functionId;
  private final String queueId;

  @AssistedInject
  AddQueue(
      LambdaAsyncClient receiver,
      BiFunction<String, String, CreateEventSourceMappingRequest> requester,
      Consumer<Throwable> logger,
      @Assisted("functionId") String functionId,
      @Assisted("queueId") String queueId) {
    this.receiver = receiver;
    this.requester = requester;
    this.logger = logger;
    this.functionId = functionId;
    this.queueId = queueId;
  }

  public static Command<Void> create(String functionId, String queueId) {
    return factory.create(functionId, queueId);
  }

  @Override
  public Void run() {
    CreateEventSourceMappingRequest request = requester.apply(functionId, queueId);
    Future<CreateEventSourceMappingResponse> response = receiver.createEventSourceMapping(request);
    ResourceUtil.logIfError(response, logger);
    return null;
  }
}
