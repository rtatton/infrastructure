package org.cirrus.infrastructure.factory;

import org.cirrus.infrastructure.util.Keys;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.ITable;
import software.amazon.awscdk.services.dynamodb.Table;
import software.constructs.Construct;

public final class NodeRegistryFactory {

  private static final Attribute PARTITION_KEY = getPartitionKey();
  private static final Number WRITE_CAPACITY = 5;
  private static final Number READ_CAPACITY = 5;
  private static final BillingMode BILLING_MODE = BillingMode.PAY_PER_REQUEST;

  public static ITable create(Construct scope) {
    return Table.Builder.create(scope, Keys.NODE_TABLE_NAME)
        .tableName(Keys.NODE_TABLE_NAME)
        .partitionKey(PARTITION_KEY)
        .writeCapacity(WRITE_CAPACITY)
        .readCapacity(READ_CAPACITY)
        .billingMode(BILLING_MODE)
        .build();
  }

  private static Attribute getPartitionKey() {
    return Attribute.builder().name(Keys.NODE_KEY).type(AttributeType.STRING).build();
  }
}
