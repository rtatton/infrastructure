package org.cirrus.infrastructure.handler;

import java.time.Duration;
import javax.inject.Inject;
import javax.inject.Named;
import org.cirrus.infrastructure.handler.util.Mapper;
import org.cirrus.infrastructure.handler.util.Resources;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

final class UploadCodeCommand implements Command<UploadCodeRequest, UploadCodeResponse> {

  private final S3Presigner signer;
  private final Mapper mapper;
  private final String bucket;
  private final String contentType;
  private final Duration signatureTtl;

  @Inject
  public UploadCodeCommand(
      S3Presigner signer,
      Mapper mapper,
      @Named("uploadBucket") String bucket,
      @Named("uploadContentType") String contentType,
      @Named("uploadSignatureTtl") Duration signatureTtl) {
    this.signer = signer;
    this.mapper = mapper;
    this.bucket = bucket;
    this.contentType = contentType;
    this.signatureTtl = signatureTtl;
  }

  @Override
  public UploadCodeResponse run(UploadCodeRequest request) {
    String key = Resources.createRandomId();
    return UploadCodeResponse.builder()
        .codeBucket(bucket)
        .codeKey(key)
        .uploadUrl(signedUrl(key))
        .build();
  }

  @Override
  public String runFromString(String input) {
    UploadCodeRequest request = mapper.read(input, UploadCodeRequest.class);
    UploadCodeResponse response = run(request);
    return mapper.write(response);
  }

  private String signedUrl(String key) {
    return signer.presignPutObject(request(key)).url().toString();
  }

  private PutObjectPresignRequest request(String key) {
    return PutObjectPresignRequest.builder()
        .putObjectRequest(
            builder -> builder.contentType(contentType).bucket(bucket).key(key).build())
        .signatureDuration(signatureTtl)
        .build();
  }
}
