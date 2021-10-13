package org.cirrus.infrastructure.handler.resource;

import org.cirrus.infrastructure.handler.util.Resource;
import org.immutables.value.Value;

@Value.Immutable
public abstract class CreateResourceOutput {

  public static Builder newBuilder() {
    return ImmutableCreateResourceOutput.newBuilder();
  }

  public abstract String getName();

  public abstract String getResourceId();

  public abstract Resource getType();

  public interface Builder {

    CreateResourceOutput build();

    Builder setName(String name);

    Builder setResourceId(String resourceId);

    Builder setType(Resource type);
  }
}
