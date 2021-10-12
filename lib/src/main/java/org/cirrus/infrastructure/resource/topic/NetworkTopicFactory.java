package org.cirrus.infrastructure.resource.topic;

import software.amazon.awscdk.services.sns.ITopic;
import software.amazon.awscdk.services.sns.Topic;
import software.constructs.Construct;

public class NetworkTopicFactory {

  private static final String DISPLAY_NAME = "NetworkTopic";
  private final Construct scope;

  private NetworkTopicFactory(Construct scope) {
    this.scope = scope;
  }

  public static NetworkTopicFactory of(Construct scope) {
    return new NetworkTopicFactory(scope);
  }

  public ITopic create() {
    return Topic.Builder.create(scope, DISPLAY_NAME).displayName(DISPLAY_NAME).build();
  }
}
