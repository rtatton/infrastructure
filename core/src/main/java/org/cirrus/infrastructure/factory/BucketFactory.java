package org.cirrus.infrastructure.factory;

import java.util.List;
import org.cirrus.infrastructure.util.Context;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.RemovalPolicy;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.IBucket;
import software.amazon.awscdk.services.s3.deployment.BucketDeployment;
import software.amazon.awscdk.services.s3.deployment.ISource;
import software.amazon.awscdk.services.s3.deployment.Source;

public final class BucketFactory {

  private static final String RUNTIME_DEPLOYMENT = "RuntimeBucketDeployment";
  private static final String RUNTIME_BUCKET = "RuntimeBucket";
  private static final String CODE_UPLOAD_BUCKET = "CodeUploadBucket";
  private static final String RUNTIME_SOURCE_PATH = "RUNTIME_SOURCE_PATH";

  private BucketFactory() {
    // no-op
  }

  public static void runtimeDeployment(Construct scope, Context context, IBucket runtimeBucket) {
    BucketDeployment.Builder.create(scope, RUNTIME_DEPLOYMENT)
        .destinationBucket(runtimeBucket)
        .memoryLimit(128)
        .sources(sources(context))
        .build();
  }

  public static IBucket runtimeBucket(Construct scope) {
    return Bucket.Builder.create(scope, RUNTIME_BUCKET)
        .removalPolicy(RemovalPolicy.DESTROY)
        .autoDeleteObjects(true)
        .enforceSsl(true)
        .build();
  }

  public static IBucket codeUploadBucket(Construct scope) {
    return Bucket.Builder.create(scope, CODE_UPLOAD_BUCKET)
        .removalPolicy(RemovalPolicy.DESTROY)
        .autoDeleteObjects(true)
        .enforceSsl(true)
        .build();
  }

  private static List<ISource> sources(Context context) {
    String sourcePath = context.get(RUNTIME_SOURCE_PATH);
    return List.of(Source.asset(sourcePath));
  }
}
