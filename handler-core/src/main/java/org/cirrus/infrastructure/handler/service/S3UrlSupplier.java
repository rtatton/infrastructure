package org.cirrus.infrastructure.handler.service;

import java.time.Duration;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Named;
import org.cirrus.infrastructure.handler.util.Resources;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

public class S3UrlSupplier implements Supplier<String> {

  private final S3Presigner signer;
  private final String bucketName;
  private final String contentType;
  private final Duration signatureTtl;

  @Inject
  public S3UrlSupplier(
      S3Presigner signer,
      @Named("codeUploadLocation") String bucketName,
      @Named("codeUploadContentType") String contentType,
      @Named("codeUploadSignatureTtl") Duration signatureTtl) {
    this.signer = signer;
    this.bucketName = bucketName;
    this.contentType = contentType;
    this.signatureTtl = signatureTtl;
  }

  @Override
  public String get() {
    return signer.presignPutObject(request()).url().toString();
  }

  private PutObjectPresignRequest request() {
    return PutObjectPresignRequest.builder()
        .putObjectRequest(
            builder ->
                builder
                    .contentType(contentType)
                    .bucket(bucketName)
                    .key(Resources.createRandomId())
                    .build())
        .signatureDuration(signatureTtl)
        .build();
  }
}
