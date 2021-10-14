package org.cirrus.infrastructure.resource.topic;

import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.services.sns.ITopic;
import software.amazon.awscdk.services.stepfunctions.TaskInput;
import software.amazon.awscdk.services.stepfunctions.TaskStateBase;
import software.amazon.awscdk.services.stepfunctions.tasks.SnsPublish;
import software.constructs.Construct;

public final class NotifyStateFactory {

  private static final String CONSTRUCT_ID = "NotifyNetwork";
  private static final String NOTIFY_NETWORK_COMMENT = "Notifies all nodes of an event";
  private static final Duration TIMEOUT = Duration.seconds(3);
  private final Construct scope;
  private final ITopic networkTopic;

  private NotifyStateFactory(Construct scope, ITopic networkTopic) {
    this.scope = scope;
    this.networkTopic = networkTopic;
  }

  public static NotifyStateFactory of(Construct scope, ITopic networkTopic) {
    return new NotifyStateFactory(scope, networkTopic);
  }

  public TaskStateBase newNotifyNetworkState() {
    return SnsPublish.Builder.create(scope, CONSTRUCT_ID)
        .topic(networkTopic)
        .message(TaskInput.fromText("")) // TODO
        .timeout(TIMEOUT)
        .comment(NOTIFY_NETWORK_COMMENT)
        .build();
  }
}
