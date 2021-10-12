package org.cirrus.infrastructure.task.topic;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.inject.Inject;
import org.cirrus.infrastructure.task.Command;
import org.cirrus.infrastructure.task.util.ResourceUtil;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;

public final class CreateTopic implements Command<String> {

  private static final TopicComponent component = DaggerTopicComponent.create();
  private final SnsAsyncClient receiver;
  private final Supplier<CreateTopicRequest> requester;
  private final Consumer<Throwable> logger;

  @Inject
  CreateTopic(
      SnsAsyncClient receiver, Supplier<CreateTopicRequest> requester, Consumer<Throwable> logger) {
    this.receiver = receiver;
    this.requester = requester;
    this.logger = logger;
  }

  public static Command<String> create() {
    return component.newCreateTopic();
  }

  @Override
  public String run() {
    CreateTopicRequest request = requester.get();
    Future<CreateTopicResponse> response = receiver.createTopic(request);
    return ResourceUtil.getIdOrNull(response, CreateTopicResponse::topicArn, logger);
  }
}
