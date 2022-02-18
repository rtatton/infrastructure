package org.cirrus.infrastructure.handler.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import java.util.Set;
import org.immutables.value.Value;
import software.amazon.awssdk.services.lambda.model.Runtime;

@Value.Immutable
public abstract class FunctionConfig {

  private static final Range<Integer> MEMORY_SIZE_RANGE = Range.closed(128, 10_240);
  private static final Range<Integer> TIMEOUT_RANGE = Range.closed(0, 900);
  private static final Set<String> RUNTIMES =
      Runtime.knownValues().stream().map(Runtime::toString).collect(ImmutableSet.toImmutableSet());

  public static Builder builder() {
    return ImmutableFunctionConfig.builder();
  }

  public abstract String codeBucket();

  public abstract String codeKey();

  @Value.Check
  protected void check() {
    Preconditions.checkState(!Strings.isNullOrEmpty(handlerName()));
    Preconditions.checkState(MEMORY_SIZE_RANGE.contains(memorySizeMegabytes()));
    Preconditions.checkState(RUNTIMES.contains(runtime()));
    Preconditions.checkState(TIMEOUT_RANGE.contains(timeoutSeconds()));
  }

  public abstract String handlerName();

  public abstract int memorySizeMegabytes();

  public abstract String runtime();

  public abstract int timeoutSeconds();

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
