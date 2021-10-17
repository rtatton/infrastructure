package org.cirrus.infrastructure.factory;

import software.amazon.awscdk.services.sns.ITopic;
import software.amazon.awscdk.services.sns.Topic;
import software.constructs.Construct;

public class NetworkTopicFactory {

  private static final String TOPIC_ID = "NetworkTopic";
  private final Construct scope;

  private NetworkTopicFactory(Construct scope) {
    this.scope = scope;
  }

  public static NetworkTopicFactory of(Construct scope) {
    return new NetworkTopicFactory(scope);
  }

  public ITopic create() {
    return Topic.Builder.create(scope, TOPIC_ID).displayName(TOPIC_ID).build();
  }
}
