package org.cirrus.infrastructure.workflow;

import com.google.common.base.Preconditions;
import java.util.Map;
import org.cirrus.infrastructure.util.Keys;
import org.cirrus.infrastructure.util.Resource;
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

public class CreateNodeStepFunction extends Construct {

  private static final String CONSTRUCT_ID = "CreateNodeStepFunctionConstruct";
  private static final String STEP_FUNCTION_ID = "CreateNodeStepFunction";
  private static final String CREATE_RESOURCES = "CreateResources";
  private static final String INTEGRATE_RESOURCES = "IntegrateResources";
  private static final String DELETE_RESOURCES = "DeleteResources";
  private static final String INTEGRATE_OR_DELETE = "IntegrateOrDeleteResources";
  private static final String SUCCESS = "Success";
  private static final String FAILURE = "Failure";
  private static final StateMachineType TYPE = StateMachineType.STANDARD;
  private final Construct scope;
  private final FunctionStateFactory functionStateFactory;
  private final QueueStateFactory queueStateFactory;
  private final TopicStateFactory topicStateFactory;
  private final NotifyStateFactory notifyStateFactory;
  private final StorageStateFactory storageStateFactory;

  private CreateNodeStepFunction(Builder builder) {
    super(builder.scope, CONSTRUCT_ID);
    this.scope = builder.scope;
    this.functionStateFactory = builder.functionStateFactory;
    this.queueStateFactory = builder.queueStateFactory;
    this.topicStateFactory = builder.topicStateFactory;
    this.notifyStateFactory = builder.notifyStateFactory;
    this.storageStateFactory = builder.storageStateFactory;
    createStateMachine();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  private void createStateMachine() {
    StateMachine.Builder.create(scope, STEP_FUNCTION_ID)
        .stateMachineName(STEP_FUNCTION_ID)
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
    IChainable[] ordered = sort(createFunction(), createQueue(), createTopic());
    return Parallel.Builder.create(scope, CREATE_RESOURCES).build().branch(ordered);
  }

  private IChainable integrateResourcesElseDeleteThenFail() {
    return Choice.Builder.create(scope, INTEGRATE_OR_DELETE)
        .build()
        .when(anyNull(), deleteResourcesThenFail())
        .otherwise(integrateResources().addCatch(deleteResourcesThenFail()));
  }

  private IChainable[] sort(IChainable function, IChainable queue, IChainable topic) {
    Map<Resource, IChainable> tasks =
        Map.of(Resource.FUNCTION, function, Resource.QUEUE, queue, Resource.TOPIC, topic);
    return Keys.sort(tasks).toArray(new IChainable[3]);
  }

  private IChainable addQueueToFunction() {
    return functionStateFactory.newAddQueueState();
  }

  private IChainable subscribeQueueToTopic() {
    return topicStateFactory.newSubscribeQueueState();
  }

  private IChainable createFunction() {
    return functionStateFactory.newCreateFunctionState();
  }

  private IChainable createQueue() {
    return queueStateFactory.newCreateQueueState();
  }

  private IChainable createTopic() {
    return topicStateFactory.newCreateTopicState();
  }

  private Parallel integrateResources() {
    IChainable[] tasks = sort(addQueueToFunction(), subscribeQueueToTopic());
    return Parallel.Builder.create(scope, INTEGRATE_RESOURCES).build().branch(tasks);
  }

  private IChainable[] sort(IChainable function, IChainable topic) {
    Map<Resource, IChainable> tasks = Map.of(Resource.FUNCTION, function, Resource.TOPIC, topic);
    return Keys.sort(tasks).toArray(new IChainable[2]);
  }

  private Condition anyNull() {
    Condition[] conditions =
        sort(
            isNull(Keys.FUNCTION_KEY_PATH),
            isNull(Keys.QUEUE_KEY_PATH),
            isNull(Keys.TOPIC_KEY_PATH));
    return Condition.or(conditions);
  }

  private Condition[] sort(Condition function, Condition queue, Condition topic) {
    Map<Resource, Condition> conditions =
        Map.of(Resource.FUNCTION, function, Resource.QUEUE, queue, Resource.TOPIC, topic);
    return Keys.sort(conditions).toArray(new Condition[3]);
  }

  private Condition isNull(String variable) {
    return Condition.isNull(variable);
  }

  private Parallel deleteResources() {
    IChainable[] tasks = sort(deleteFunction(), deleteQueue(), deleteTopic());
    return Parallel.Builder.create(scope, DELETE_RESOURCES).build().branch(tasks);
  }

  private IChainable deleteResourcesThenFail() {
    return deleteResources().next(fail());
  }

  private TaskStateBase storeResourceIds() {
    return storageStateFactory.newStoreResourceIdsState();
  }

  private TaskStateBase storeIdsElseDeleteThenFail() {
    return storeResourceIds().addCatch(deleteResourcesThenFail());
  }

  private IChainable deleteFunction() {
    return functionStateFactory.newDeleteFunctionState();
  }

  private IChainable deleteQueue() {
    return queueStateFactory.newDeleteQueueState();
  }

  private IChainable deleteTopic() {
    return topicStateFactory.newDeleteTopicState();
  }

  private TaskStateBase notifyNetwork() {
    return notifyStateFactory.newNotifyNetworkState();
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
    return storageStateFactory.newDeleteResourceIdsState();
  }

  private IChainable fail() {
    return Fail.Builder.create(scope, FAILURE).build();
  }

  public static class Builder {

    private Construct scope;
    private FunctionStateFactory functionStateFactory;
    private QueueStateFactory queueStateFactory;
    private TopicStateFactory topicStateFactory;
    private NotifyStateFactory notifyStateFactory;
    private StorageStateFactory storageStateFactory;

    private Builder() {}

    public CreateNodeStepFunction build() {
      checkAttributes();
      return new CreateNodeStepFunction(this);
    }

    private void checkAttributes() {
      Preconditions.checkNotNull(scope);
      Preconditions.checkNotNull(functionStateFactory);
      Preconditions.checkNotNull(queueStateFactory);
      Preconditions.checkNotNull(topicStateFactory);
      Preconditions.checkNotNull(notifyStateFactory);
      Preconditions.checkNotNull(storageStateFactory);
    }

    public Builder setScope(Construct scope) {
      this.scope = scope;
      return this;
    }

    public Builder setFunctionStateFactory(FunctionStateFactory functionStateFactory) {
      this.functionStateFactory = functionStateFactory;
      return this;
    }

    public Builder setQueueStateFactory(QueueStateFactory queueStateFactory) {
      this.queueStateFactory = queueStateFactory;
      return this;
    }

    public Builder setTopicStateFactory(TopicStateFactory topicStateFactory) {
      this.topicStateFactory = topicStateFactory;
      return this;
    }

    public Builder setNotifyStateFactory(NotifyStateFactory notifyStateFactory) {
      this.notifyStateFactory = notifyStateFactory;
      return this;
    }

    public Builder setStorageStateFactory(StorageStateFactory storageStateFactory) {
      this.storageStateFactory = storageStateFactory;
      return this;
    }
  }
}
