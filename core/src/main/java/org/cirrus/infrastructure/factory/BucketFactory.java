package org.cirrus.infrastructure.factory;

import java.util.List;
import org.cirrus.infrastructure.util.Keys;
import org.immutables.builder.Builder;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.IBucket;
import software.amazon.awscdk.services.s3.deployment.BucketDeployment;
import software.amazon.awscdk.services.s3.deployment.Source;
import software.constructs.Construct;

final class BucketFactory {

  private BucketFactory() {
    // no-op
  }

  @Builder.Factory
  public static BucketDeployment runtimeDeployment(
      @Builder.Parameter Construct scope, IBucket runtimeBucket) {
    return BucketDeployment.Builder.create(scope, null)
        .destinationBucket(runtimeBucket)
        .memoryLimit(128)
        .sources(List.of(Source.asset(""))) // TODO - build locally or use release
        .build();
  }

  @Builder.Factory
  public static IBucket runtimeBucket(@Builder.Parameter Construct scope) {
    return Bucket.Builder.create(scope, Keys.RUNTIME_BUCKET)
        .removalPolicy(RemovalPolicy.DESTROY)
        .autoDeleteObjects(true)
        .enforceSsl(true)
        .build();
  }

  @Builder.Factory
  public static IBucket codeUploadBucket(@Builder.Parameter Construct scope) {
    return Bucket.Builder.create(scope, Keys.CODE_UPLOAD_BUCKET)
        .removalPolicy(RemovalPolicy.DESTROY)
        .autoDeleteObjects(true)
        .enforceSsl(true)
        .build();
  }
}
