package org.cirrus.infrastructure.handler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Map;
import org.cirrus.infrastructure.util.Keys;
import org.cirrus.infrastructure.util.Preconditions;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableFunctionConfig.class)
@JsonDeserialize(as = ImmutableFunctionConfig.class)
public abstract class FunctionConfig {

  public static Builder builder() {
    return ImmutableFunctionConfig.builder();
  }

  @Value.Default
  public int memorySizeMegabytes() {
    return 128;
  }

  @Value.Default
  public int timeoutSeconds() {
    return 60;
  }

  @JsonProperty(Keys.ARTIFACT_ID)
  public abstract String artifactId();

  public abstract Map<String, String> environment();

  @Value.Check
  protected void check() {
    Preconditions.checkInRangeClosed(memorySizeMegabytes(), 128, 10240);
    Preconditions.checkInRangeClosed(timeoutSeconds(), 0, 900);
    Preconditions.checkNotNullOrEmpty(artifactId());
  }

  public abstract static class Builder {

    public abstract FunctionConfig build();

    public abstract Builder memorySizeMegabytes(int memorySize);

    public abstract Builder timeoutSeconds(int timeout);

    public abstract Builder artifactId(String artifactId);

    public abstract Builder putAllEnvironment(Map<String, ? extends String> variables);

    public abstract Builder putEnvironment(Map.Entry<String, ? extends String> variable);

    public abstract Builder putEnvironment(String key, String value);
  }
}
