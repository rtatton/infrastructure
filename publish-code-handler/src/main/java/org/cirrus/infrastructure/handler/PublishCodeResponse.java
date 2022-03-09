package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cirrus.infrastructure.util.Keys;
import org.immutables.value.Value;

@Value.Immutable
public interface PublishCodeResponse {

  static Builder builder() {
    return ImmutablePublishCodeResponse.builder();
  }

  static PublishCodeResponse of(String codeUri) {
    return ImmutablePublishCodeResponse.of(codeUri);
  }

  @JsonProperty(Keys.CODE_URI)
  String codeUri();

  interface Builder {

    Builder codeUri(String codeUri);

    PublishCodeResponse build();
  }
}
