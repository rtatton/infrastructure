package org.cirrus.infrastructure.handler;

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

  @Value.Check
  protected void check() {
    Preconditions.checkState(!Strings.isNullOrEmpty(getFunctionName()));
    Preconditions.checkState(MEMORY_SIZE_RANGE.contains(getMemorySizeInMegabytes()));
    Preconditions.checkState(RUNTIMES.contains(getRuntime()));
    Preconditions.checkState(TIMEOUT_RANGE.contains(getTimeoutInSeconds()));
  }

  public abstract String getFunctionName();

  public abstract int getMemorySizeInMegabytes();

  public abstract String getRuntime();

  public abstract int getTimeoutInSeconds();

  public abstract String getCodeBucket();

  public abstract String getCodeKey();

  public interface Builder {
    FunctionConfig build();

    Builder setFunctionName(String functionName);

    Builder setMemorySizeInMegabytes(int memorySize);

    Builder setTimeoutInSeconds(int timeout);

    Builder setRuntime(String runtime);

    Builder setCodeBucket(String codeBucket);

    Builder setCodeKey(String codeKey);
  }
}
