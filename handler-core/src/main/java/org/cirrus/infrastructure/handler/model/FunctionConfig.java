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
  @JsonProperty(Keys.FUNCTION_MEMORY_SIZE)
  public int memorySizeMegabytes() {
    return 128;
  }

  @Value.Default
  @JsonProperty(Keys.FUNCTION_TIMEOUT)
  public int timeoutSeconds() {
    return 60;
  }

  @JsonProperty(Keys.CODE_ID)
  public abstract String codeId();

  @JsonProperty(Keys.FUNCTION_ENVIRONMENT)
  public abstract Map<String, String> environment();

  @Value.Check
  protected void check() {
    Preconditions.checkInRangeClosed(memorySizeMegabytes(), 128, 10240);
    Preconditions.checkInRangeClosed(timeoutSeconds(), 0, 900);
    Preconditions.checkNotNullOrEmpty(codeId());
  }

  public interface Builder {

    FunctionConfig build();

    Builder memorySizeMegabytes(int memorySize);

    Builder timeoutSeconds(int timeout);

    Builder codeId(String codeId);

    Builder putAllEnvironment(Map<String, ? extends String> variables);

    Builder putEnvironment(Map.Entry<String, ? extends String> variable);

    Builder putEnvironment(String key, String value);
  }
}
