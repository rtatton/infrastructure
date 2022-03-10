package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import java.util.stream.Collectors;
import org.cirrus.infrastructure.util.Keys;
import org.cirrus.infrastructure.util.Preconditions;
import org.immutables.value.Value;
import software.amazon.awssdk.services.lambda.model.Runtime;

@Value.Immutable
public abstract class PublishCodeRequest {

  private static final Set<String> RUNTIMES =
      Runtime.knownValues().stream().map(Runtime::toString).collect(Collectors.toUnmodifiableSet());

  public static Builder builder() {
    return ImmutablePublishCodeRequest.builder();
  }

  @JsonProperty(Keys.CODE_KEY)
  public abstract String codeKey();

  @JsonProperty(Keys.FUNCTION_RUNTIME)
  public abstract String runtime();

  @Value.Check
  protected void check() {
    Preconditions.notNullOrEmpty(codeKey());
    Preconditions.checkState(RUNTIMES.contains(runtime()));
  }

  public interface Builder {

    Builder codeKey(String codeKey);

    Builder runtime(String runtime);

    PublishCodeRequest build();
  }
}
