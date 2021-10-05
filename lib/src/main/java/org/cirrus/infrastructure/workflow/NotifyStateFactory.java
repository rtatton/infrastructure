package org.cirrus.infrastructure.workflow;

import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.services.sns.ITopic;
import software.amazon.awscdk.services.stepfunctions.TaskInput;
import software.amazon.awscdk.services.stepfunctions.TaskStateBase;
import software.amazon.awscdk.services.stepfunctions.tasks.SnsPublish;
import software.constructs.Construct;

public final class NotifyStateFactory {

  private static final String NOTIFY_NETWORK = "NotifyNetwork";
  private static final String NOTIFY_NETWORK_COMMENT = "Notifies all nodes of an event";
  private static final Duration TIMEOUT = Duration.seconds(3);
  private final ITopic networkTopic; // TODO

  private NotifyStateFactory(ITopic networkTopic) {
    this.networkTopic = networkTopic;
  }

  public static NotifyStateFactory of(ITopic networkTopic) {
    return new NotifyStateFactory(networkTopic);
  }

  public TaskStateBase notifyNetwork(Construct scope) {
    return SnsPublish.Builder.create(scope, NOTIFY_NETWORK)
        .topic(networkTopic)
        .message(TaskInput.fromText("")) // TODO
        .timeout(TIMEOUT)
        .comment(NOTIFY_NETWORK_COMMENT)
        .build();
  }
}
