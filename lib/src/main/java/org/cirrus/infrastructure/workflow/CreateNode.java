package org.cirrus.infrastructure.workflow;

import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.services.stepfunctions.Choice;
import software.amazon.awscdk.services.stepfunctions.Condition;
import software.amazon.awscdk.services.stepfunctions.Fail;
import software.amazon.awscdk.services.stepfunctions.IChainable;
import software.amazon.awscdk.services.stepfunctions.INextable;
import software.amazon.awscdk.services.stepfunctions.Parallel;
import software.amazon.awscdk.services.stepfunctions.StateMachine;
import software.amazon.awscdk.services.stepfunctions.StateMachineType;
import software.amazon.awscdk.services.stepfunctions.Succeed;
import software.amazon.awscdk.services.stepfunctions.TaskStateBase;
import software.constructs.Construct;

public class CreateNode extends Stack {

  private static final String CREATE_NODE_STACK = "CreateNodeStack";
  private static final String CREATE_NODE = "CreateNode";
  private static final String CREATE_RESOURCES = "CreateResources";
  private static final String INTEGRATE_RESOURCES = "IntegrateResources";
  private static final String DELETE_RESOURCES = "DeleteResources";
  private static final String INTEGRATE_OR_DELETE = "IntegrateOrDeleteResources";
  private static final String SUCCESS = "Success";
  private static final String FAILURE = "Failure";
  private static final String FUNCTION = "function";
  private static final String QUEUE = "queue";
  private static final String TOPIC = "topic";
  private static final StateMachineType TYPE = StateMachineType.STANDARD;
  private final Construct scope;
  private final FunctionStateFactory functionStateFactory;
  private final QueueStateFactory queueStateFactory;
  private final TopicStateFactory topicStateFactory;
  private final NotifyStateFactory notifyStateFactory;
  private final StorageStateFactory storageStateFactory;

  public CreateNode(
      Construct scope,
      FunctionStateFactory functionStateFactory,
      QueueStateFactory queueStateFactory,
      TopicStateFactory topicStateFactory,
      NotifyStateFactory notifyStateFactory,
      StorageStateFactory storageStateFactory) {
    super(scope, CREATE_NODE_STACK);
    this.scope = scope;
    this.functionStateFactory = functionStateFactory;
    this.queueStateFactory = queueStateFactory;
    this.topicStateFactory = topicStateFactory;
    this.notifyStateFactory = notifyStateFactory;
    this.storageStateFactory = storageStateFactory;
    createStateMachine();
  }

  private void createStateMachine() {
    StateMachine.Builder.create(scope, CREATE_NODE)
        .stateMachineName(CREATE_NODE)
        .stateMachineType(TYPE)
        .definition(definition())
        .build();
  }

  private IChainable definition() {
    return createResources()
        .next(integrateResourcesElseDeleteThenFail())
        .next(storeIdsElseDeleteThenFail())
        .next(notifyElseDeleteThenFail())
        .next(succeed());
  }

  private INextable createResources() {
    return Parallel.Builder.create(scope, CREATE_RESOURCES)
        .build()
        .branch(createFunction(), createQueue(), createTopic());
  }

  private IChainable integrateResourcesElseDeleteThenFail() {
    return Choice.Builder.create(scope, INTEGRATE_OR_DELETE)
        .build()
        .when(anyNull(), deleteResourcesThenFail())
        .otherwise(integrateResources().addCatch(deleteResourcesThenFail()));
  }

  private Parallel integrateResources() {
    return Parallel.Builder.create(scope, INTEGRATE_RESOURCES)
        .build()
        .branch(addQueueToFunction(), subscribeQueueToTopic());
  }

  private IChainable addQueueToFunction() {
    return functionStateFactory.getAddQueueState();
  }

  private IChainable subscribeQueueToTopic() {
    return topicStateFactory.getSubscribeQueueState();
  }

  private IChainable createFunction() {
    return functionStateFactory.getCreateFunctionState();
  }

  private IChainable createQueue() {
    return queueStateFactory.getCreateQueueState();
  }

  private IChainable createTopic() {
    return topicStateFactory.getCreateTopicState();
  }

  private Condition anyNull() {
    return Condition.or(isNull(FUNCTION), isNull(QUEUE), isNull(TOPIC));
  }

  private Parallel deleteResources() {
    return Parallel.Builder.create(scope, DELETE_RESOURCES)
        .build()
        .branch(deleteFunction(), deleteQueue(), deleteTopic());
  }

  private IChainable deleteResourcesThenFail() {
    return deleteResources().next(fail());
  }

  private TaskStateBase storeResourceIds() {
    return storageStateFactory.storeResourceIds();
  }

  private TaskStateBase storeIdsElseDeleteThenFail() {
    return storeResourceIds().addCatch(deleteResourcesThenFail());
  }

  private Condition isNull(String variable) {
    return Condition.isNull(variable);
  }

  private IChainable deleteFunction() {
    return functionStateFactory.getDeleteFunctionState();
  }

  private IChainable deleteQueue() {
    return queueStateFactory.getDeleteQueueState();
  }

  private IChainable deleteTopic() {
    return topicStateFactory.getDeleteTopicState();
  }

  private TaskStateBase notifyNetwork() {
    return notifyStateFactory.getNotifyNetworkState();
  }

  private IChainable notifyElseDeleteThenFail() {
    return notifyNetwork().addCatch(deleteResourcesThenIdsThenFail());
  }

  private IChainable succeed() {
    return Succeed.Builder.create(scope, SUCCESS).build();
  }

  private IChainable deleteResourcesThenIdsThenFail() {
    return deleteResources().next(deleteResourceIds()).next(fail());
  }

  private IChainable deleteResourceIds() {
    return storageStateFactory.deleteResourceIds();
  }

  private IChainable fail() {
    return Fail.Builder.create(scope, FAILURE).build();
  }
}
