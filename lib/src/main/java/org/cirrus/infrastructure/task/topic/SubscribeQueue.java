package org.cirrus.infrastructure.task.topic;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.cirrus.infrastructure.task.Command;
import org.cirrus.infrastructure.task.util.ResourceUtil;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;

public final class SubscribeQueue implements Command<Void> {

  private static final SubscribeQueueFactory factory =
      DaggerTopicComponent.create().getSubscribeQueueFactory();
  private final SnsAsyncClient receiver;
  private final BiFunction<String, String, SubscribeRequest> requester;
  private final Consumer<Throwable> logger;
  private final String topicId;
  private final String queueId;

  @AssistedInject
  SubscribeQueue(
      SnsAsyncClient receiver,
      BiFunction<String, String, SubscribeRequest> requester,
      Consumer<Throwable> logger,
      @Assisted("topicId") String topicId,
      @Assisted("queueId") String queueId) {
    this.receiver = receiver;
    this.requester = requester;
    this.logger = logger;
    this.topicId = topicId;
    this.queueId = queueId;
  }

  public static Command<Void> create(String topicId, String queueId) {
    return factory.create(topicId, queueId);
  }

  @Override
  public Void run() {
    SubscribeRequest request = requester.apply(topicId, queueId);
    Future<SubscribeResponse> response = receiver.subscribe(request);
    ResourceUtil.logIfError(response, logger);
    return null;
  }
}
