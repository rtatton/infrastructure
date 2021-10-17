package org.cirrus.infrastructure.handler;

import java.util.concurrent.Future;
import java.util.function.Supplier;
import javax.inject.Inject;
import org.cirrus.infrastructure.util.Command;
import org.cirrus.infrastructure.util.Logger;
import org.cirrus.infrastructure.util.ResourceUtil;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;

public final class CreateTopicCommand implements Command<String> {

  private static final CreateTopicComponent component = DaggerCreateTopicComponent.create();
  private final SnsAsyncClient receiver;
  private final Supplier<CreateTopicRequest> requester;
  private final Logger logger;

  @Inject
  CreateTopicCommand(
      SnsAsyncClient receiver, Supplier<CreateTopicRequest> requester, Logger logger) {
    this.receiver = receiver;
    this.requester = requester;
    this.logger = logger;
  }

  public static Command<String> create() {
    return component.getCreateTopicCommand();
  }

  @Override
  public String run() {
    CreateTopicRequest request = requester.get();
    Future<CreateTopicResponse> response = receiver.createTopic(request);
    return ResourceUtil.getOrNull(response, CreateTopicResponse::topicArn, logger);
  }
}
