package org.cirrus.infrastructure.handler.resource;

import org.immutables.value.Value;

@Value.Immutable
public abstract class CreateResourceInput {

  public static Builder newBuilder() {
    return ImmutableCreateResourceInput.newBuilder();
  }

  public abstract String getName();

  public interface Builder {

    CreateResourceInput build();

    Builder setName(String name);
  }
}
