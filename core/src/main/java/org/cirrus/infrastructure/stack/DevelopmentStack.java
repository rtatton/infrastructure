package org.cirrus.infrastructure.stack;

import org.cirrus.infrastructure.factory.ApiBuilder;
import org.cirrus.infrastructure.factory.CodeBucketBuilder;
import org.cirrus.infrastructure.factory.NodeRoleBuilder;
import org.cirrus.infrastructure.factory.NodeTableBuilder;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.dynamodb.ITable;
import software.amazon.awscdk.services.iam.IRole;
import software.amazon.awscdk.services.s3.IBucket;
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
    ApiBuilder.create(scope)
        .nodeTable(nodeTable())
        .codeBucket(codeBucket())
        .nodeRole(nodeRole())
        .build();
  }

  private ITable nodeTable() {
    return NodeTableBuilder.create(scope).build();
  }

  private IBucket codeBucket() {
    return CodeBucketBuilder.create(scope).build();
  }

  private IRole nodeRole() {
    return NodeRoleBuilder.create(scope).build();
  }
}
