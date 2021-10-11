package org.cirrus.infrastructure.task.queue;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.inject.Inject;
import org.cirrus.infrastructure.task.util.Command;
import org.cirrus.infrastructure.task.util.Resources;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;

public final class CreateQueue implements Command<String> {

  private static final QueueComponent component = DaggerQueueComponent.create();
  private final SqsAsyncClient receiver;
  private final Supplier<CreateQueueRequest> requester;
  private final Consumer<Throwable> logger;

  @Inject
  CreateQueue(
      SqsAsyncClient receiver, Supplier<CreateQueueRequest> requester, Consumer<Throwable> logger) {
    this.receiver = receiver;
    this.requester = requester;
    this.logger = logger;
  }

  public static Command<String> create() {
    return component.newCreateQueue();
  }

  @Override
  public String run() {
    CreateQueueRequest createQueueRequest = requester.get();
    Future<CreateQueueResponse> response = receiver.createQueue(createQueueRequest);
    return Resources.getIdOrNull(response, CreateQueueResponse::queueUrl, logger);
  }
}
