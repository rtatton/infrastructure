package org.cirrus.infrastructure.handler.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cirrus.infrastructure.util.Keys;
import org.immutables.value.Value;

@Value.Immutable
public abstract class CreateResourceInput {

  public static Builder newBuilder() {
    return ImmutableCreateResourceInput.newBuilder();
  }

  @JsonProperty(Keys.NODE_INPUT_KEY)
  public abstract String getName();

  public interface Builder {

    CreateResourceInput build();

    Builder setName(String name);
  }
}
