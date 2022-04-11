package org.cirrus.infrastructure.stack;

import org.cirrus.infrastructure.factory.CodeUploadBucketBuilder;
import org.cirrus.infrastructure.factory.NodeApiBuilder;
import org.cirrus.infrastructure.factory.NodeRoleBuilder;
import org.cirrus.infrastructure.factory.NodeTableBuilder;
import org.cirrus.infrastructure.factory.RuntimeBucketBuilder;
import org.cirrus.infrastructure.factory.RuntimeDeploymentBuilder;
import software.amazon.awscdk.CfnParameter;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.dynamodb.ITable;
import software.amazon.awscdk.services.iam.IRole;
import software.amazon.awscdk.services.s3.IBucket;
import software.constructs.Construct;

public class DevelopmentStack extends Stack {
  private static final String RUNTIME_SOURCE_PATH = "runtimeSourcePath";
  private static final String STACK_ID = "DevStack";

  public DevelopmentStack(Construct scope, StackProps props) {
    super(scope, STACK_ID, props);
    createResources();
  }

  private void createResources() {
    NodeApiBuilder.create(this)
        .nodeTable(nodeTable())
        .uploadBucket(codeUploadBucket())
        .runtimeBucket(runtimeBucket())
        .nodeRole(nodeRole())
        .build();
    RuntimeDeploymentBuilder.create(this)
        .runtimeBucket(runtimeBucket())
        .sourcePath(runtimeSourcePath())
        .build();
  }

  private ITable nodeTable() {
    return NodeTableBuilder.create(this).build();
  }

  private IBucket runtimeBucket() {
    return RuntimeBucketBuilder.create(this).build();
  }

  private IBucket codeUploadBucket() {
    return CodeUploadBucketBuilder.create(this).build();
  }

  private IRole nodeRole() {
    return NodeRoleBuilder.create(this).build();
  }

  private String runtimeSourcePath() {
    CfnParameter runtimeSourcePath =
        CfnParameter.Builder.create(this, RUNTIME_SOURCE_PATH)
            .type("String")
            .description("Path to the source asset of the Lambda runtime")
            .build();
    return runtimeSourcePath.getValueAsString();
  }
}
