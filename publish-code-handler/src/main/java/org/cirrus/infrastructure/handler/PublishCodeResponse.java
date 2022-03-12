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

  public static PublishCodeResponse of(String artifactId) {
    return ImmutablePublishCodeResponse.of(artifactId);
  }

  @Value.Parameter
  @JsonProperty(Keys.ARTIFACT_ID)
  public abstract String artifactId();

  @Value.Check
  protected void check() {
    Preconditions.checkNotNullOrEmpty(artifactId());
  }

  public interface Builder {

    Builder artifactId(String artifactId);

    PublishCodeResponse build();
  }
}
