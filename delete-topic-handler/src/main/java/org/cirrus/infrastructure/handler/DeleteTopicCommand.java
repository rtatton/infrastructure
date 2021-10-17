package org.cirrus.infrastructure.handler;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import java.util.concurrent.Future;
import java.util.function.Function;
import org.cirrus.infrastructure.util.Command;
import org.cirrus.infrastructure.util.Logger;
import org.cirrus.infrastructure.util.ResourceUtil;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.DeleteTopicRequest;
import software.amazon.awssdk.services.sns.model.DeleteTopicResponse;

public final class DeleteTopicCommand implements Command<Void> {

  private static final DeleteTopicComponent component = DaggerDeleteTopicComponent.create();
  private final SnsAsyncClient receiver;
  private final Function<String, DeleteTopicRequest> requester;
  private final Logger logger;
  private final String topicId;

  @AssistedInject
  DeleteTopicCommand(
      SnsAsyncClient receiver,
      Function<String, DeleteTopicRequest> requester,
      Logger logger,
      @Assisted String topicId) {
    this.receiver = receiver;
    this.requester = requester;
    this.logger = logger;
    this.topicId = topicId;
  }

  public static Command<Void> create(String topicId) {
    return component.getDeleteTopicCommandFactory().create(topicId);
  }

  @Override
  public Void run() {
    DeleteTopicRequest request = requester.apply(topicId);
    Future<DeleteTopicResponse> response = receiver.deleteTopic(request);
    ResourceUtil.logIfError(response, logger);
    return null;
  }
}
