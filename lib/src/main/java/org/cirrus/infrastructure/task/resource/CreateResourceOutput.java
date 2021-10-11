package org.cirrus.infrastructure.task.resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableCreateResourceOutput.class)
@JsonDeserialize(as = ImmutableCreateResourceOutput.class)
public abstract class CreateResourceOutput {

  public static Builder newBuilder() {
    return ImmutableCreateResourceOutput.newBuilder();
  }

  public abstract String getName();

  public abstract String getResourceId();

  public abstract ResourceType getType();

  public interface Builder {

    CreateResourceOutput build();

    Builder setName(String name);

    Builder setResourceId(String resourceId);

    Builder setType(ResourceType type);
  }
}
