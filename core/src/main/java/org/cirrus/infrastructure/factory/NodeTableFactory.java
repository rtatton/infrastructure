package org.cirrus.infrastructure.factory;

import org.cirrus.infrastructure.util.Keys;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.ITable;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.dynamodb.TableEncryption;

public final class NodeTableFactory {

  private NodeTableFactory() {
    // no-op
  }

  public static ITable create(Construct scope) {
    return Table.Builder.create(scope, Keys.NODE_TABLE_NAME)
        .partitionKey(partitionKey())
        .billingMode(BillingMode.PAY_PER_REQUEST)
        .encryption(TableEncryption.AWS_MANAGED)
        .build();
  }

  private static Attribute partitionKey() {
    return Attribute.builder().name(Keys.NODE_ID).type(AttributeType.STRING).build();
  }
}
