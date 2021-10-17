package org.cirrus.infrastructure.factory;

import org.cirrus.infrastructure.util.Keys;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.ITable;
import software.amazon.awscdk.services.dynamodb.Table;
import software.constructs.Construct;

public class NodeRegistryFactory {

  private static final String TABLE_ID = "NodeRegistry";
  private static final Attribute PARTITION_KEY = getPartitionKey();
  private static final Number WRITE_CAPACITY = 5;
  private static final Number READ_CAPACITY = 5;
  private static final BillingMode BILLING_MODE = BillingMode.PAY_PER_REQUEST;
  private final Construct scope;

  private NodeRegistryFactory(Construct scope) {
    this.scope = scope;
  }

  public static NodeRegistryFactory of(Construct scope) {
    return new NodeRegistryFactory(scope);
  }

  public ITable create() {
    return Table.Builder.create(scope, TABLE_ID)
        .tableName(TABLE_ID)
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
