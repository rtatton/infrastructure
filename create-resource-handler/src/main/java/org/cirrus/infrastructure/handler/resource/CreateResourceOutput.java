package org.cirrus.infrastructure.handler.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cirrus.infrastructure.util.Keys;
import org.cirrus.infrastructure.util.Resource;
import org.immutables.value.Value;

@Value.Immutable
public abstract class CreateResourceOutput {

  public static Builder newBuilder() {
    return ImmutableCreateResourceOutput.newBuilder();
  }

  @JsonProperty(Keys.NODE_INPUT_KEY)
  public abstract String getName();

  @JsonProperty(Keys.RESOURCE_KEY)
  public abstract String getResourceId();

  @JsonProperty(Keys.TYPE_KEY)
  public abstract Resource getType();

  public interface Builder {

    CreateResourceOutput build();

    Builder setName(String name);

    Builder setResourceId(String resourceId);

    Builder setType(Resource type);
  }
}
