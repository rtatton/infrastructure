package org.cirrus.infrastructure.stack;

import org.cirrus.infrastructure.factory.BucketFactory;
import org.cirrus.infrastructure.factory.IamFactory;
import org.cirrus.infrastructure.factory.NodeApiBuilder;
import org.cirrus.infrastructure.factory.NodeTableFactory;
import org.cirrus.infrastructure.util.Context;
import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.s3.IBucket;

public class DevelopmentStack extends Stack {

  private static final String STACK_ID = "DevStack";

  public DevelopmentStack(App scope, StackProps props) {
    super(scope, STACK_ID, props);
    createResources(Context.of(scope.getNode()));
  }

  private void createResources(Context context) {
    IBucket runtimeBucket = BucketFactory.runtimeBucket(this);
    BucketFactory.runtimeDeployment(this, context, runtimeBucket);
    NodeApiBuilder.create(this)
        .nodeTable(NodeTableFactory.create(this))
        .uploadBucket(BucketFactory.codeUploadBucket(this))
        .runtimeBucket(runtimeBucket)
        .nodeRole(IamFactory.nodeRole(this))
        .build();
  }
}
