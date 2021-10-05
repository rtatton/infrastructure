package org.cirrus.infrastructure.workflow;

import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.services.dynamodb.ITable;
import software.amazon.awscdk.services.sns.ITopic;
import software.amazon.awscdk.services.stepfunctions.Choice;
import software.amazon.awscdk.services.stepfunctions.Condition;
import software.amazon.awscdk.services.stepfunctions.Fail;
import software.amazon.awscdk.services.stepfunctions.IChainable;
import software.amazon.awscdk.services.stepfunctions.INextable;
import software.amazon.awscdk.services.stepfunctions.Parallel;
import software.amazon.awscdk.services.stepfunctions.State;
import software.amazon.awscdk.services.stepfunctions.StateMachine;
import software.amazon.awscdk.services.stepfunctions.StateMachineType;
import software.amazon.awscdk.services.stepfunctions.Succeed;
import software.constructs.Construct;

public class CreateNode extends Stack {

  private static final String CREATE_NODE = "CreateNode";
  private static final String CREATE_RESOURCES = "CreateResources";
  private static final String DELETE_RESOURCES = "DeleteResources";
  private static final String SUCCESS = "SuccessfulNodeCreation";
  private static final String FAILURE = "FailedNodeCreation";
  private static final String FUNCTION = "function";
  private static final String QUEUE = "queue";
  private static final String TOPIC = "topic";
  private static final String EMPTY = "";
  private static final StateMachineType TYPE = StateMachineType.STANDARD;
  private final Construct scope;
  private final ITopic networkTopic;
  private final ITable nodeRegistry;

  public CreateNode(Construct scope, String id, ITopic networkTopic, ITable nodeRegistry) {
    super(scope, id);
    this.scope = scope;
    this.networkTopic = networkTopic;
    this.nodeRegistry = nodeRegistry;
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
    return createResources().next(choice());
  }

  private INextable createResources() {
    return Parallel.Builder.create(scope, CREATE_RESOURCES)
        .build()
        .branch(createFunction(), createQueue(), createTopic());
  }

  private State choice() {
    return Choice.Builder.create(scope, "")
        .build()
        .when(anyEmpty(), deleteResources())
        .otherwise(storeResourceIds());
  }

  private IChainable createFunction() {
    return FunctionStateFactory.of().createFunction(scope);
  }

  private IChainable createQueue() {
    return QueueStateFactory.of().createQueue(scope);
  }

  private IChainable createTopic() {
    return TopicStateFactory.of().createTopic(scope);
  }

  private Condition anyEmpty() {
    return Condition.or(isEmpty(FUNCTION), isEmpty(QUEUE), isEmpty(TOPIC));
  }

  private Parallel deleteResources() {
    return Parallel.Builder.create(scope, DELETE_RESOURCES)
        .build()
        .branch(deleteFunction(), deleteQueue(), deleteTopic());
  }

  private IChainable storeResourceIds() {
    return StorageStateFactory.of(nodeRegistry)
        .storeResourceIds(scope)
        .addCatch(deleteResources())
        .next(notifyNetwork())
        .next(success());
  }

  private Condition isEmpty(String variable) {
    return Condition.stringEquals(variable, EMPTY);
  }

  private IChainable deleteFunction() {
    return FunctionStateFactory.of().deleteFunction(scope);
  }

  private IChainable deleteQueue() {
    return QueueStateFactory.of().deleteQueue(scope);
  }

  private IChainable deleteTopic() {
    return TopicStateFactory.of().deleteTopic(scope);
  }

  private IChainable notifyNetwork() {
    return NotifyStateFactory.of(networkTopic)
        .notifyNetwork(scope)
        .addCatch(deleteResourcesAndIds());
  }

  private IChainable success() {
    return Succeed.Builder.create(scope, SUCCESS).build();
  }

  private IChainable deleteResourcesAndIds() {
    return deleteResources().next(deleteResourceIds()).next(failure());
  }

  private IChainable deleteResourceIds() {
    return StorageStateFactory.of(nodeRegistry).deleteResourceIds(scope);
  }

  private IChainable failure() {
    return Fail.Builder.create(scope, FAILURE).build();
  }
}
