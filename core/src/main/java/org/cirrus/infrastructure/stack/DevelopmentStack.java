package org.cirrus.infrastructure.stack;

import org.cirrus.infrastructure.factory.NodeApiFactory;
import org.cirrus.infrastructure.factory.NodeTableFactory;
import software.amazon.awscdk.Stack;
import software.constructs.Construct;

public class DevelopmentStack extends Stack {

  private static final String STACK_ID = "DevStack";
  private final Construct scope;

  public DevelopmentStack(Construct scope) {
    super(scope, STACK_ID);
    this.scope = scope;
    createResources();
  }

  private void createResources() {
    nodeTable();
    nodeApi();
  }

  private void nodeTable() {
    NodeTableFactory.create(scope);
  }

  private void nodeApi() {
    NodeApiFactory.create(scope);
  }
}
