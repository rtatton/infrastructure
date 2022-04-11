package org.cirrus.infrastructure.stack;

import org.cirrus.infrastructure.factory.CodeUploadBucketBuilder;
import org.cirrus.infrastructure.factory.NodeApiBuilder;
import org.cirrus.infrastructure.factory.NodeRoleBuilder;
import org.cirrus.infrastructure.factory.NodeTableBuilder;
import org.cirrus.infrastructure.factory.RuntimeBucketBuilder;
import org.cirrus.infrastructure.factory.RuntimeDeploymentBuilder;
import software.amazon.awscdk.CfnParameter;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.dynamodb.ITable;
import software.amazon.awscdk.services.iam.IRole;
import software.amazon.awscdk.services.s3.IBucket;
import software.constructs.Construct;

public class DevelopmentStack extends Stack {
  private static final String RUNTIME_SOURCE_PATH = "runtimeSourcePath";
  private static final String STACK_ID = "DevStack";

  public DevelopmentStack(Construct scope) {
    super(scope, STACK_ID);
    createResources(scope);
  }

  private static void createResources(Construct scope) {
    NodeApiBuilder.create(scope)
        .nodeTable(nodeTable(scope))
        .uploadBucket(codeUploadBucket(scope))
        .nodeRole(nodeRole(scope))
        .build();
    RuntimeDeploymentBuilder.create(scope)
        .runtimeBucket(runtimeBucket(scope))
        .sourcePath(runtimeSourcePath(scope))
        .build();
  }

  private static ITable nodeTable(Construct scope) {
    return NodeTableBuilder.create(scope).build();
  }

  private static IBucket runtimeBucket(Construct scope) {
    return RuntimeBucketBuilder.create(scope).build();
  }

  private static IBucket codeUploadBucket(Construct scope) {
    return CodeUploadBucketBuilder.create(scope).build();
  }

  private static IRole nodeRole(Construct scope) {
    return NodeRoleBuilder.create(scope).build();
  }

  private static String runtimeSourcePath(Construct scope) {
    CfnParameter runtimeSourcePath =
        CfnParameter.Builder.create(scope, RUNTIME_SOURCE_PATH)
            .type("String")
            .description("Path to the source asset of the Lambda runtime")
            .build();
    return runtimeSourcePath.getValueAsString();
  }
}
