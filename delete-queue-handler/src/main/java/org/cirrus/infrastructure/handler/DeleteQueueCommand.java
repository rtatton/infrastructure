package org.cirrus.infrastructure.handler;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import java.util.concurrent.Future;
import java.util.function.Function;
import org.cirrus.infrastructure.util.Command;
import org.cirrus.infrastructure.util.Logger;
import org.cirrus.infrastructure.util.ResourceUtil;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteQueueRequest;
import software.amazon.awssdk.services.sqs.model.DeleteQueueResponse;

public final class DeleteQueueCommand implements Command<Void> {

  private static final DeleteQueueComponent component = DaggerDeleteQueueComponent.create();
  private final SqsAsyncClient receiver;
  private final Function<String, DeleteQueueRequest> requester;
  private final Logger logger;
  private final String queueId;

  @AssistedInject
  DeleteQueueCommand(
      SqsAsyncClient receiver,
      Function<String, DeleteQueueRequest> requester,
      Logger logger,
      @Assisted String queueId) {
    this.receiver = receiver;
    this.requester = requester;
    this.logger = logger;
    this.queueId = queueId;
  }

  public static Command<Void> create(String queueId) {
    return component.getDeleteQueueCommandFactory().create(queueId);
  }

  @Override
  public Void run() {
    DeleteQueueRequest request = requester.apply(queueId);
    Future<DeleteQueueResponse> response = receiver.deleteQueue(request);
    ResourceUtil.logIfError(response, logger);
    return null;
  }
}
