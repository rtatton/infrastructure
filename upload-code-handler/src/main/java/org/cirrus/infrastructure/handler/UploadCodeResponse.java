package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cirrus.infrastructure.util.Keys;
import org.cirrus.infrastructure.util.Preconditions;
import org.immutables.value.Value;

@Value.Immutable
public abstract class UploadCodeResponse {

  public static Builder builder() {
    return ImmutableUploadCodeResponse.builder();
  }

  @JsonProperty(Keys.CODE_UPLOAD_URL)
  public abstract String uploadUrl();

  @JsonProperty(Keys.CODE_ID)
  public abstract String codeId();

  @Value.Check
  protected void check() {
    Preconditions.checkNotNullOrEmpty(uploadUrl());
    Preconditions.checkNotNullOrEmpty(codeId());
  }

  public interface Builder {

    Builder uploadUrl(String uploadUrl);

    Builder codeId(String codeId);

    UploadCodeResponse build();
  }
}
