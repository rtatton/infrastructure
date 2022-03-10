package org.cirrus.infrastructure.factory;

import org.cirrus.infrastructure.util.Keys;
import org.immutables.builder.Builder;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.IBucket;
import software.constructs.Construct;

final class CodeBucketFactory {

  private CodeBucketFactory() {
    // no-op
  }

  @Builder.Factory
  public static IBucket codeBucket(@Builder.Parameter Construct scope) {
    return Bucket.Builder.create(scope, Keys.CODE_UPLOAD_BUCKET)
        .removalPolicy(RemovalPolicy.DESTROY)
        .autoDeleteObjects(true)
        .enforceSsl(true)
        .build();
  }
}
