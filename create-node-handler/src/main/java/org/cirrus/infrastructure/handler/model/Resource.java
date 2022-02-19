package org.cirrus.infrastructure.handler.model;

import javax.annotation.Nullable;
import org.immutables.value.Value;

@Value.Immutable
public abstract class Resource {

  public static Builder builder() {
    return ImmutableResource.builder();
  }

  @Nullable
  public abstract String id();

  @Nullable
  public abstract RuntimeException exception();

  public interface Builder {

    Resource build();

    Builder id(String id);

    Builder exception(RuntimeException exception);
  }
}
