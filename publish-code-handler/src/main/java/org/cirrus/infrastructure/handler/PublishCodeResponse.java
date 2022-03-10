package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cirrus.infrastructure.util.Keys;
import org.cirrus.infrastructure.util.Preconditions;
import org.immutables.value.Value;

@Value.Immutable
public abstract class PublishCodeResponse {

  public static Builder builder() {
    return ImmutablePublishCodeResponse.builder();
  }

  public static PublishCodeResponse of(String codeUri) {
    return ImmutablePublishCodeResponse.of(codeUri);
  }

  @JsonProperty(Keys.CODE_URI)
  public abstract String codeUri();

  @Value.Check
  protected void check() {
    Preconditions.notNullOrEmpty(codeUri());
  }

  public interface Builder {

    Builder codeUri(String codeUri);

    PublishCodeResponse build();
  }
}
