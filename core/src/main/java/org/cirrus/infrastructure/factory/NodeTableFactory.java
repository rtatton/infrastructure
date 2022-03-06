package org.cirrus.infrastructure.factory;

import org.cirrus.infrastructure.util.Keys;
import org.immutables.builder.Builder;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.ITable;
import software.amazon.awscdk.services.dynamodb.Table;
import software.constructs.Construct;

final class NodeTableFactory {

  private NodeTableFactory() {
    // no-op
  }

  @Builder.Factory
  public static ITable nodeTable(@Builder.Parameter Construct scope) {
    return Table.Builder.create(scope, Keys.NODE_TABLE_NAME)
        .partitionKey(getPartitionKey())
        .writeCapacity(5)
        .readCapacity(5)
        .billingMode(BillingMode.PAY_PER_REQUEST)
        .build();
  }

  private static Attribute getPartitionKey() {
    return Attribute.builder().name(Keys.NODE_ID).type(AttributeType.STRING).build();
  }
}
