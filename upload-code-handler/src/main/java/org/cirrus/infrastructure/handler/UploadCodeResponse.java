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

  @JsonProperty(Keys.UPLOAD_URL)
  public abstract String uploadUrl();

  @JsonProperty(Keys.CODE_KEY)
  public abstract String codeKey();

  @Value.Check
  protected void check() {
    Preconditions.notNullOrEmpty(uploadUrl());
    Preconditions.notNullOrEmpty(codeKey());
  }

  public interface Builder {

    Builder uploadUrl(String uploadUrl);

    Builder codeKey(String codeKey);

    UploadCodeResponse build();
  }
}
