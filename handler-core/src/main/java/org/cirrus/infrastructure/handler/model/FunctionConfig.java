package org.cirrus.infrastructure.handler.model;

import java.util.Set;
import java.util.stream.Collectors;
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

  public abstract String codeBucket();

  public abstract String codeKey();

  public abstract String handlerName();

  public abstract String runtime();

  @Value.Default
  public int memorySizeMegabytes() {
    return 128;
  }

  @Value.Default
  public int timeoutSeconds() {
    return 3;
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
