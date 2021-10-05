package org.cirrus.infrastructure.workflow;

import java.util.Map;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.services.dynamodb.ITable;
import software.amazon.awscdk.services.stepfunctions.TaskStateBase;
import software.amazon.awscdk.services.stepfunctions.tasks.DynamoDeleteItem;
import software.amazon.awscdk.services.stepfunctions.tasks.DynamoPutItem;
import software.constructs.Construct;

public final class StorageStateFactory {

  private static final String STORE_RESOURCE_IDS = "StoreResourceIds";
  private static final String STORE_RESOURCE_IDS_COMMENT =
      "Stores the Lambda function, SQS queue, and SNS topic IDs in the node registry";
  private static final String DELETE_RESOURCE_IDS = "StoreResourceIds";
  private static final String DELETE_RESOURCE_IDS_COMMENT =
      "Stores the Lambda function, SQS queue, and SNS topic IDs in the node registry";
  private static final Duration TIMEOUT = Duration.seconds(3);
  private final Construct scope;
  private final ITable nodeRegistry; // TODO

  private StorageStateFactory(Construct scope, ITable nodeRegistry) {
    this.scope = scope;
    this.nodeRegistry = nodeRegistry;
  }

  public static StorageStateFactory of(Construct scope, ITable nodeRegistry) {
    return new StorageStateFactory(scope, nodeRegistry);
  }

  public TaskStateBase storeResourceIds() {
    return DynamoPutItem.Builder.create(scope, STORE_RESOURCE_IDS)
        .table(nodeRegistry)
        .item(Map.of()) // TODO
        .timeout(TIMEOUT)
        .comment(STORE_RESOURCE_IDS_COMMENT)
        .build();
  }

  public TaskStateBase deleteResourceIds() {
    return DynamoDeleteItem.Builder.create(scope, DELETE_RESOURCE_IDS)
        .table(nodeRegistry)
        .comment(DELETE_RESOURCE_IDS_COMMENT)
        .timeout(TIMEOUT)
        .key(Map.of()) // TODO
        .build();
  }
}
