package org.cirrus.infrastructure.handler.queue;

import java.util.concurrent.Future;
import java.util.function.Supplier;
import javax.inject.Inject;
import org.cirrus.infrastructure.util.Command;
import org.cirrus.infrastructure.util.Logger;
import org.cirrus.infrastructure.util.ResourceUtil;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;

public final class CreateQueueCommand implements Command<String> {

  private static final CreateQueueComponent component = DaggerCreateQueueComponent.create();
  private final SqsAsyncClient receiver;
  private final Supplier<CreateQueueRequest> requester;
  private final Logger logger;

  @Inject
  CreateQueueCommand(
      SqsAsyncClient receiver, Supplier<CreateQueueRequest> requester, Logger logger) {
    this.receiver = receiver;
    this.requester = requester;
    this.logger = logger;
  }

  public static Command<String> create() {
    return component.getCreateQueueCommand();
  }

  @Override
  public String run() {
    CreateQueueRequest createQueueRequest = requester.get();
    Future<CreateQueueResponse> response = receiver.createQueue(createQueueRequest);
    return ResourceUtil.getOrNull(response, CreateQueueResponse::queueUrl, logger);
  }
}
