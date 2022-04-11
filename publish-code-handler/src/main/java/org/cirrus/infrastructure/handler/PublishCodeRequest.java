package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Set;
import java.util.stream.Collectors;
import org.cirrus.infrastructure.util.Keys;
import org.cirrus.infrastructure.util.Preconditions;
import org.immutables.value.Value;
import software.amazon.awssdk.services.lambda.model.Runtime;

@Value.Immutable
@JsonSerialize(as = ImmutablePublishCodeRequest.class)
@JsonDeserialize(as = ImmutablePublishCodeRequest.class)
public abstract class PublishCodeRequest {

  private static final Set<String> RUNTIMES =
      Runtime.knownValues().stream().map(Runtime::toString).collect(Collectors.toUnmodifiableSet());

  public static Builder builder() {
    return ImmutablePublishCodeRequest.builder();
  }

  @JsonProperty(Keys.CODE_ID)
  public abstract String codeId();

  public abstract String runtime();

  @Value.Check
  protected void check() {
    Preconditions.checkNotNullOrEmpty(codeId());
    Preconditions.checkNotNullOrEmpty(runtime());
    Preconditions.checkState(RUNTIMES.contains(runtime()));
  }

  public abstract static class Builder {

    public abstract Builder codeId(String codeId);

    public abstract Builder runtime(String runtime);

    public abstract PublishCodeRequest build();
  }
}
