package org.cirrus.infrastructure.stack;

import org.cirrus.infrastructure.factory.ApiFactory;
import org.cirrus.infrastructure.factory.NodeTableFactory;
import software.amazon.awscdk.Stack;
import software.constructs.Construct;

public class DevelopmentStack extends Stack {

  private static final String STACK_ID = "DevStack";
  private final Construct scope;

  public DevelopmentStack(Construct scope) {
    super(scope, STACK_ID);
    this.scope = scope;
    createStackResources();
  }

  private void createStackResources() {
    nodeRegistry();
    api();
  }

  private void nodeRegistry() {
    NodeTableFactory.create(scope);
  }

  private void api() {
    ApiFactory.create(scope);
  }
}
