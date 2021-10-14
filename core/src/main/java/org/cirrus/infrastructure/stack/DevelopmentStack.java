package org.cirrus.infrastructure.stack;

import org.cirrus.infrastructure.resource.function.FunctionStateFactory;
import org.cirrus.infrastructure.resource.queue.QueueStateFactory;
import org.cirrus.infrastructure.resource.storage.NodeRegistryFactory;
import org.cirrus.infrastructure.resource.storage.StorageStateFactory;
import org.cirrus.infrastructure.resource.topic.NetworkTopicFactory;
import org.cirrus.infrastructure.resource.topic.NotifyStateFactory;
import org.cirrus.infrastructure.resource.topic.TopicStateFactory;
import org.cirrus.infrastructure.workflow.CreateNodeStepFunction;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.services.dynamodb.ITable;
import software.amazon.awscdk.services.sns.ITopic;
import software.constructs.Construct;

public class DevelopmentStack extends Stack {

  private static final String STACK_ID = "DevStack";
  private final Construct scope;

  public DevelopmentStack(Construct scope) {
    super(scope, STACK_ID);
    this.scope = scope;
    createStackResources();
  }

  private void createStackResources() {
    CreateNodeStepFunction.newBuilder()
        .setScope(scope)
        .setFunctionStateFactory(createFunctionStateFactory())
        .setQueueStateFactory(createQueueStateFactory())
        .setTopicStateFactory(createTopicStateFactory())
        .setNotifyStateFactory(createNotifyStateFactory())
        .setStorageStateFactory(createStorageStateFactory())
        .build();
  }

  private FunctionStateFactory createFunctionStateFactory() {
    return FunctionStateFactory.of(scope);
  }

  private QueueStateFactory createQueueStateFactory() {
    return QueueStateFactory.of(scope);
  }

  private TopicStateFactory createTopicStateFactory() {
    return TopicStateFactory.of(scope);
  }

  private NotifyStateFactory createNotifyStateFactory() {
    return NotifyStateFactory.of(scope, createNetworkTopic());
  }

  private StorageStateFactory createStorageStateFactory() {
    return StorageStateFactory.of(scope, createNodeRegistry());
  }

  private ITopic createNetworkTopic() {
    return NetworkTopicFactory.of(scope).create();
  }

  private ITable createNodeRegistry() {
    return NodeRegistryFactory.of(scope).create();
  }
}
