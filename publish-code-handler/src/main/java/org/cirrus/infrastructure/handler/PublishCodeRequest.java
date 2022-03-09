package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cirrus.infrastructure.util.Keys;
import org.immutables.value.Value;

@Value.Immutable
public interface PublishCodeRequest {

  static Builder builder() {
    return ImmutablePublishCodeRequest.builder();
  }

  @JsonProperty(Keys.CODE_KEY)
  String codeKey();

  @JsonProperty(Keys.FUNCTION_RUNTIME)
  String runtime();

  interface Builder {

    Builder codeKey(String codeKey);

    Builder runtime(String runtime);

    PublishCodeRequest build();
  }
}
