package org.cirrus.infrastructure.task.topic;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import org.cirrus.infrastructure.task.util.Command;
import org.cirrus.infrastructure.task.util.ResourceUtil;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.DeleteTopicRequest;
import software.amazon.awssdk.services.sns.model.DeleteTopicResponse;

public final class DeleteTopic implements Command<Void> {

  private static final DeleteTopicFactory factory =
      DaggerTopicComponent.create().newDeleteTopicFactory();
  private final SnsAsyncClient receiver;
  private final Function<String, DeleteTopicRequest> requester;
  private final Consumer<Throwable> logger;
  private final String topicId;

  @AssistedInject
  DeleteTopic(
      SnsAsyncClient receiver,
      Function<String, DeleteTopicRequest> requester,
      Consumer<Throwable> logger,
      @Assisted String topicId) {
    this.receiver = receiver;
    this.requester = requester;
    this.logger = logger;
    this.topicId = topicId;
  }

  public static Command<Void> create(String topicId) {
    return factory.create(topicId);
  }

  @Override
  public Void run() {
    DeleteTopicRequest request = requester.apply(topicId);
    Future<DeleteTopicResponse> response = receiver.deleteTopic(request);
    ResourceUtil.logIfError(response, logger);
    return null;
  }
}
