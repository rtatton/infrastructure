package org.cirrus.infrastructure.factory;

import org.cirrus.infrastructure.util.Keys;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.ITable;
import software.amazon.awscdk.services.dynamodb.Table;
import software.constructs.Construct;

public final class NodeTableFactory {

  private NodeTableFactory() {
    // no-op
  }

  public static ITable create(Construct scope) {
    return Table.Builder.create(scope, Keys.NODE_TABLE_NAME)
        .tableName(Keys.NODE_TABLE_NAME)
        .partitionKey(getPartitionKey())
        .writeCapacity(5)
        .readCapacity(5)
        .billingMode(BillingMode.PAY_PER_REQUEST)
        .build();
  }

  private static Attribute getPartitionKey() {
    return Attribute.builder().name(Keys.NODE_KEY).type(AttributeType.STRING).build();
  }
}
