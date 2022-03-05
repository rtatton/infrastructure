package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cirrus.infrastructure.util.Keys;
import org.immutables.value.Value;

@Value.Immutable
interface UploadCodeResponse {

  static Builder builder() {
    return ImmutableUploadCodeResponse.builder();
  }

  @JsonProperty(Keys.UPLOAD_URL)
  String uploadUrl();

  @JsonProperty(Keys.CODE_BUCKET)
  String codeBucket();

  @JsonProperty(Keys.CODE_KEY)
  String codeKey();

  interface Builder {

    Builder uploadUrl(String uploadUrl);

    Builder codeBucket(String codeBucket);

    Builder codeKey(String codeKey);

    UploadCodeResponse build();
  }
}
