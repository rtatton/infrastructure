package org.cirrus.infrastructure.task.resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableCreateResourceInput.class)
@JsonDeserialize(as = ImmutableCreateResourceInput.class)
public abstract class CreateResourceInput {

  public static Builder newBuilder() {
    return ImmutableCreateResourceInput.newBuilder();
  }

  public abstract String getName();

  public abstract ResourceType getType();

  public interface Builder {

    CreateResourceInput build();

    Builder setName(String name);
  }
}
