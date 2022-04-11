package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cirrus.infrastructure.util.Keys;
import org.cirrus.infrastructure.util.Preconditions;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableUploadCodeResponse.class)
@JsonDeserialize(as = ImmutableUploadCodeResponse.class)
public abstract class UploadCodeResponse {

  public static Builder builder() {
    return ImmutableUploadCodeResponse.builder();
  }

  public abstract String uploadUrl();

  @JsonProperty(Keys.CODE_ID)
  public abstract String codeId();

  @Value.Check
  protected void check() {
    Preconditions.checkNotNullOrEmpty(uploadUrl());
    Preconditions.checkNotNullOrEmpty(codeId());
  }

  public abstract static class Builder {

    public abstract Builder uploadUrl(String uploadUrl);

    public abstract Builder codeId(String codeId);

    public abstract UploadCodeResponse build();
  }
}
