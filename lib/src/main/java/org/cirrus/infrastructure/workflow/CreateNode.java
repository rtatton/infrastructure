package org.cirrus.infrastructure.workflow;

import java.util.Map;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.services.dynamodb.ITable;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.sns.ITopic;
import software.amazon.awscdk.services.stepfunctions.Fail;
import software.amazon.awscdk.services.stepfunctions.IChainable;
import software.amazon.awscdk.services.stepfunctions.State;
import software.amazon.awscdk.services.stepfunctions.StateMachine;
import software.amazon.awscdk.services.stepfunctions.StateMachineType;
import software.amazon.awscdk.services.stepfunctions.Succeed;
import software.amazon.awscdk.services.stepfunctions.TaskInput;
import software.amazon.awscdk.services.stepfunctions.TaskStateBase;
import software.amazon.awscdk.services.stepfunctions.tasks.DynamoPutItem;
import software.amazon.awscdk.services.stepfunctions.tasks.SnsPublish;
import software.constructs.Construct;

public class CreateNode extends Stack {

  private static final String CREATE_NODE = "CreateNode";
  private static final String CREATE_FUNCTION = "CreateFunction"; // TODO
  private static final String CREATE_FUNCTION_COMMENT = "Creates a Lambda function for a new node";
  private static final String CREATE_FUNCTION_PATH = ""; // TODO
  private static final String CREATE_QUEUE = "CreateQueue"; // TODO
  private static final String CREATE_QUEUE_PATH = ""; // TODO
  private static final String CREATE_QUEUE_COMMENT =
      "Creates an SQS queue for a new node and adds it as an event source to the Lambda function";
  private static final String CREATE_TOPIC = "CreateTopic"; // TODO
  private static final String CREATE_TOPIC_PATH = ""; // TODO
  private static final String CREATE_TOPIC_COMMENT =
      "Creates an SNS topic for a new node and subscribes it to the network topic";
  private static final String STORE_RESOURCE_IDS = "StoreResourceIds";
  private static final String NOTIFY_NETWORK = "NotifyNetwork";
  private static final String NOTIFY_NETWORK_COMMENT =
      "Notifies all nodes in the network that a new node was created";
  private static final String HANDLE_FUNCTION_FAILURE = "HandleCreateFunctionFailure";
  private static final String HANDLE_FUNCTION_FAILURE_PATH = ""; // TODO
  private static final String HANDLE_FUNCTION_FAILURE_COMMENT =
      "Attempts to delete the created Lambda function and/or logs the failure event";
  private static final String HANDLE_QUEUE_FAILURE = "HandleCreateQueueFailure";
  private static final String HANDLE_QUEUE_FAILURE_PATH = ""; // TODO
  private static final String HANDLE_QUEUE_FAILURE_COMMENT =
      "Attempts to delete the created SQS queue and/or logs the failure event";
  private static final String HANDLE_TOPIC_FAILURE = "HandleCreateTopicFailure";
  private static final String HANDLE_TOPIC_FAILURE_PATH = ""; // TODO
  private static final String HANDLE_TOPIC_FAILURE_COMMENT =
      "Attempts to unsubscribe the created SQS queue, delete the created SNS topic, and/or logs the failure event";
  private static final String HANDLE_STORE_RESOURCE_IDS_FAILURE = "";
  private static final String HANDLE_STORE_RESOURCE_IDS_FAILURE_PATH = ""; // TODO
  private static final String HANDLE_STORE_RESOURCE_IDS_FAILURE_COMMENT = ""; // TODO
  private static final String HANDLE_NOTIFY_NETWORK_FAILURE = "HandleNotifyNetworkFailure";
  private static final String HANDLE_NOTIFY_NETWORK_FAILURE_PATH = ""; // TODO
  private static final String HANDLE_NOTIFY_NETWORK_FAILURE_COMMENT =
      "Logs the failure to notify the network";
  private static final String STORE_RESOURCE_IDS_COMMENT =
      "Stores the Lambda function, SQS queue, and SNS topic IDs in the node registry";
  private static final Runtime RUNTIME = Runtime.JAVA_11;
  private static final Duration TIMEOUT = Duration.seconds(3);
  private static final StateMachineType TYPE = StateMachineType.STANDARD;
  private final Construct scope;
  private final ITopic networkTopic; // TODO
  private final ITable nodeRegistry; // TODO

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
    return createFunction()
        .next(createQueue())
        .next(createTopic())
        .next(storeNodeResourceIds())
        .next(notifyNetwork())
        .next(success());
  }

  private TaskStateBase createFunction() {
    return createFunctionState().addCatch(handleFunctionFailure());
  }

  private IChainable createQueue() {
    return createQueueState().addCatch(handleQueueFailure());
  }

  private IChainable createTopic() {
    return createTopicState().addCatch(handleTopicFailure());
  }

  private IChainable storeNodeResourceIds() {
    return storeResourceIdsState().addCatch(handleStoreResourceIdsFailure());
  }

  private IChainable notifyNetwork() {
    return notifyNetworkState().addCatch(handleNotifyNetworkFailure());
  }

  private IChainable handleFunctionFailure() {
    return handleFunctionFailureState().next(failure());
  }

  private IChainable handleQueueFailure() {
    return handleQueueFailureState().next(handleFunctionFailure());
  }

  private IChainable handleTopicFailure() {
    return handleTopicFailureState().next(handleQueueFailure());
  }

  private IChainable handleStoreResourceIdsFailure() {
    return handleStoreResourceIdsFailureState().next(handleTopicFailure());
  }

  private IChainable handleNotifyNetworkFailure() {
    return handleNotifyNetworkFailureState().next(handleStoreResourceIdsFailure());
  }

  private TaskStateBase createFunctionState() {
    return lambdaState(CREATE_FUNCTION, CREATE_FUNCTION_PATH, CREATE_FUNCTION_COMMENT);
  }

  private TaskStateBase createQueueState() {
    return lambdaState(CREATE_QUEUE, CREATE_QUEUE_PATH, CREATE_QUEUE_COMMENT);
  }

  private TaskStateBase createTopicState() {
    return lambdaState(CREATE_TOPIC, CREATE_TOPIC_PATH, CREATE_TOPIC_COMMENT);
  }

  private TaskStateBase storeResourceIdsState() {
    return DynamoPutItem.Builder.create(scope, STORE_RESOURCE_IDS)
        .table(nodeRegistry)
        .item(Map.of()) // TODO
        .timeout(TIMEOUT)
        .comment(STORE_RESOURCE_IDS_COMMENT)
        .build();
  }

  private TaskStateBase notifyNetworkState() {
    return SnsPublish.Builder.create(scope, NOTIFY_NETWORK)
        .topic(networkTopic)
        .message(TaskInput.fromText("")) // TODO
        .timeout(TIMEOUT)
        .comment(NOTIFY_NETWORK_COMMENT)
        .build();
  }

  private State success() {
    // TODO
    return Succeed.Builder.create(scope, "").build();
  }

  private TaskStateBase handleFunctionFailureState() {
    return lambdaState(
        HANDLE_FUNCTION_FAILURE, HANDLE_FUNCTION_FAILURE_PATH, HANDLE_FUNCTION_FAILURE_COMMENT);
  }

  private TaskStateBase handleQueueFailureState() {
    return lambdaState(
        HANDLE_QUEUE_FAILURE, HANDLE_QUEUE_FAILURE_PATH, HANDLE_QUEUE_FAILURE_COMMENT);
  }

  private TaskStateBase handleTopicFailureState() {
    return lambdaState(
        HANDLE_TOPIC_FAILURE, HANDLE_TOPIC_FAILURE_PATH, HANDLE_TOPIC_FAILURE_COMMENT);
  }

  private TaskStateBase handleStoreResourceIdsFailureState() {
    return lambdaState(
        HANDLE_STORE_RESOURCE_IDS_FAILURE,
        HANDLE_STORE_RESOURCE_IDS_FAILURE_PATH,
        HANDLE_STORE_RESOURCE_IDS_FAILURE_COMMENT);
  }

  private TaskStateBase handleNotifyNetworkFailureState() {
    return LambdaStateBuilder.create(scope)
        .setFunctionName(HANDLE_NOTIFY_NETWORK_FAILURE)
        .setCodePath(HANDLE_NOTIFY_NETWORK_FAILURE_PATH)
        .setComment(HANDLE_NOTIFY_NETWORK_FAILURE_COMMENT)
        .build();
  }

  private State failure() {
    // TODO
    return Fail.Builder.create(scope, "").build();
  }
}
