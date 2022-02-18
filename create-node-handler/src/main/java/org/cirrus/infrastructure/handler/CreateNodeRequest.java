package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cirrus.infrastructure.util.Keys;
import org.immutables.value.Value;

@Value.Immutable
public abstract class CreateNodeRequest {

  public static Builder newBuilder() {
    return ImmutableCreateNodeRequest.newBuilder();
  }

  @JsonProperty(Keys.NODE_INPUT_KEY)
  public abstract String getName();

  public abstract QueueConfig getQueueConfig();

  public abstract FunctionConfig getFunctionConfig();

  public interface Builder {

    CreateNodeRequest build();

    Builder setName(String name);
  }
}
