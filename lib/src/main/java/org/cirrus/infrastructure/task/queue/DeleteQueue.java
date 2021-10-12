package org.cirrus.infrastructure.task.queue;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import org.cirrus.infrastructure.task.Command;
import org.cirrus.infrastructure.task.util.ResourceUtil;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteQueueRequest;
import software.amazon.awssdk.services.sqs.model.DeleteQueueResponse;

public final class DeleteQueue implements Command<Void> {

  private static final DeleteQueueFactory factory =
      DaggerQueueComponent.create().getDeleteQueueFactory();
  private final SqsAsyncClient receiver;
  private final Function<String, DeleteQueueRequest> requester;
  private final Consumer<Throwable> logger;
  private final String queueId;

  @AssistedInject
  DeleteQueue(
      SqsAsyncClient receiver,
      Function<String, DeleteQueueRequest> requester,
      Consumer<Throwable> logger,
      @Assisted String queueId) {
    this.receiver = receiver;
    this.requester = requester;
    this.logger = logger;
    this.queueId = queueId;
  }

  public static Command<Void> create(String queueId) {
    return factory.create(queueId);
  }

  @Override
  public Void run() {
    DeleteQueueRequest request = requester.apply(queueId);
    Future<DeleteQueueResponse> response = receiver.deleteQueue(request);
    ResourceUtil.logIfError(response, logger);
    return null;
  }
}
