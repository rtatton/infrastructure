package org.cirrus.infrastructure.workflow;

import java.util.Map;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.services.dynamodb.ITable;
import software.amazon.awscdk.services.stepfunctions.TaskStateBase;
import software.amazon.awscdk.services.stepfunctions.tasks.DynamoAttributeValue;
import software.amazon.awscdk.services.stepfunctions.tasks.DynamoDeleteItem;
import software.amazon.awscdk.services.stepfunctions.tasks.DynamoPutItem;
import software.constructs.Construct;

public final class StorageStateFactory {

  private static final String STORE_RESOURCE_IDS = "StoreResourceIds";
  private static final String STORE_RESOURCE_IDS_COMMENT =
      "Stores the Lambda function, SQS queue, and SNS topic IDs of a node in the node registry";
  private static final String DELETE_RESOURCE_IDS = "DeleteResourceIds";
  private static final String DELETE_RESOURCE_IDS_COMMENT =
      "Deletes the Lambda function, SQS queue, and SNS topic IDs of a node in the node registry";
  private static final Duration TIMEOUT = Duration.seconds(3);
  private static final Map<String, DynamoAttributeValue> NODE_REGISTRY_ITEM = getNodeRegistryItem();
  private static final Map<String, DynamoAttributeValue> DELETE_KEY = getDeleteKey();
  private final Construct scope;
  private final ITable nodeRegistry; // TODO

  private StorageStateFactory(Construct scope, ITable nodeRegistry) {
    this.scope = scope;
    this.nodeRegistry = nodeRegistry;
  }

  public static StorageStateFactory of(Construct scope, ITable nodeRegistry) {
    return new StorageStateFactory(scope, nodeRegistry);
  }

  public TaskStateBase newStoreResourceIdsState() {
    return DynamoPutItem.Builder.create(scope, STORE_RESOURCE_IDS)
        .table(nodeRegistry)
        .item(NODE_REGISTRY_ITEM)
        .timeout(TIMEOUT)
        .comment(STORE_RESOURCE_IDS_COMMENT)
        .build();
  }

  public TaskStateBase newDeleteResourceIdsState() {
    return DynamoDeleteItem.Builder.create(scope, DELETE_RESOURCE_IDS)
        .table(nodeRegistry)
        .key(DELETE_KEY)
        .timeout(TIMEOUT)
        .comment(DELETE_RESOURCE_IDS_COMMENT)
        .build();
  }

  private static Map<String, DynamoAttributeValue> getNodeRegistryItem() {
    return Map.of(
        "nodeId", DynamoAttributeValue.fromString("$[0].name"),
        "functionId", DynamoAttributeValue.fromString("$[0].functionId"),
        "queueId", DynamoAttributeValue.fromString("$[1].queueId"),
        "topicId", DynamoAttributeValue.fromString("$[2].topicId"));
  }

  private static Map<String, DynamoAttributeValue> getDeleteKey() {
    return Map.of("nodeId", DynamoAttributeValue.fromString("$[0].name"));
  }
}
