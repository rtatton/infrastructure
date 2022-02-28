package org.cirrus.infrastructure.stack;

import org.cirrus.infrastructure.factory.NodeRegistryFactory;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.services.dynamodb.ITable;
import software.constructs.Construct;

public class DevelopmentStack extends Stack {

  private static final String STACK_ID = "DevStack";
  private final Construct scope;

  public DevelopmentStack(Construct scope) {
    super(scope, STACK_ID);
    this.scope = scope;
    createStackResources();
  }

  private void createStackResources() {}

  private ITable nodeRegistry() {
    return NodeRegistryFactory.create(scope);
  }
}
