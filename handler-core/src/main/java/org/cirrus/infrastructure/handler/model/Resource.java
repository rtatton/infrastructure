package org.cirrus.infrastructure.handler.model;

import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import org.immutables.value.Value;

@Value.Immutable
public abstract class Resource {

  public static Builder builder() {
    return ImmutableResource.builder();
  }

  @Value.Derived
  public boolean failed() {
    return id() == null && exception() != null;
  }

  @Value.Derived
  public boolean succeeded() {
    return id() != null && exception() == null;
  }

  @Nullable
  public abstract String id();

  @Nullable
  public abstract RuntimeException exception();

  @Value.Check
  protected void check() {
    // Exclusive or: at least one, but not both, is null
    Preconditions.checkState(id() != null ^ exception() != null);
  }

  public interface Builder {

    Resource build();

    Builder id(String id);

    Builder exception(RuntimeException exception);
  }
}
