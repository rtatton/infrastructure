package org.cirrus.infrastructure.handler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import java.util.stream.Collectors;
import org.cirrus.infrastructure.util.Keys;
import org.cirrus.infrastructure.util.Preconditions;
import org.immutables.value.Value;
import software.amazon.awssdk.services.lambda.model.Runtime;

@Value.Immutable
public abstract class FunctionConfig {

  private static final Set<String> RUNTIMES =
      Runtime.knownValues().stream().map(Runtime::toString).collect(Collectors.toUnmodifiableSet());

  public static Builder builder() {
    return ImmutableFunctionConfig.builder();
  }

  @JsonProperty(Keys.CODE_BUCKET)
  public abstract String codeBucket();

  @JsonProperty(Keys.CODE_KEY)
  public abstract String codeKey();

  @JsonProperty(Keys.FUNCTION_HANDLER_NAME)
  public abstract String handlerName();

  @JsonProperty(Keys.FUNCTION_RUNTIME)
  public abstract String runtime();

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

  @Value.Check
  protected void check() {
    Preconditions.notNullOrEmpty(handlerName());
    Preconditions.inRangeClosed(memorySizeMegabytes(), 128, 10240);
    Preconditions.checkState(RUNTIMES.contains(runtime()));
    Preconditions.inRangeClosed(timeoutSeconds(), 0, 900);
  }

  public interface Builder {

    FunctionConfig build();

    Builder handlerName(String handlerName);

    Builder memorySizeMegabytes(int memorySize);

    Builder timeoutSeconds(int timeout);

    Builder runtime(String runtime);

    Builder codeBucket(String codeBucket);

    Builder codeKey(String codeKey);
  }
}
