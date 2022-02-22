package org.cirrus.infrastructure.handler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cirrus.infrastructure.util.Keys;
import org.immutables.value.Value;

@Value.Immutable
public abstract class CreateNodeRequest {

  public static Builder builder() {
    return ImmutableCreateNodeRequest.builder();
  }

  @JsonProperty(Keys.NODE_KEY)
  public abstract String nodeId();

  @JsonProperty(Keys.FUNCTION_CONFIG_KEY)
  public abstract FunctionConfig functionConfig();

  @JsonProperty(Keys.QUEUE_CONFIG_KEY)
  public abstract QueueConfig queueConfig();

  public interface Builder {

    CreateNodeRequest build();

    Builder nodeId(String nodeId);

    Builder functionConfig(FunctionConfig config);

    Builder queueConfig(QueueConfig config);
  }
}
