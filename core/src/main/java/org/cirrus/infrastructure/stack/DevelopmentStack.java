package org.cirrus.infrastructure.stack;

import org.cirrus.infrastructure.factory.CodeBucketBuilder;
import org.cirrus.infrastructure.factory.NodeApiBuilder;
import org.cirrus.infrastructure.factory.NodeRoleBuilder;
import org.cirrus.infrastructure.factory.NodeTableBuilder;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.dynamodb.ITable;
import software.amazon.awscdk.services.iam.IRole;
import software.amazon.awscdk.services.s3.IBucket;
import software.constructs.Construct;

public class DevelopmentStack extends Stack {

  private static final String STACK_ID = "DevStack";

  public DevelopmentStack(Construct scope) {
    super(scope, STACK_ID);
    createResources(scope);
  }

  private static void createResources(Construct scope) {
    NodeApiBuilder.create(scope)
        .nodeTable(nodeTable(scope))
        .codeBucket(codeBucket(scope))
        .nodeRole(nodeRole(scope))
        .build();
  }

  private static ITable nodeTable(Construct scope) {
    return NodeTableBuilder.create(scope).build();
  }

  private static IBucket codeBucket(Construct scope) {
    return CodeBucketBuilder.create(scope).build();
  }

  private static IRole nodeRole(Construct scope) {
    return NodeRoleBuilder.create(scope).build();
  }
}
