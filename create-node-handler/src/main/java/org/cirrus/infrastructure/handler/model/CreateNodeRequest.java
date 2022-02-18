package org.cirrus.infrastructure.handler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cirrus.infrastructure.util.Keys;
import org.immutables.value.Value;

@Value.Immutable
public abstract class CreateNodeRequest {

  public static Builder newBuilder() {
    return ImmutableCreateNodeRequest.newBuilder();
  }

  @JsonProperty(Keys.NODE_KEY)
  public abstract String getNodeId();

  @JsonProperty(Keys.FUNCTION_CONFIG_KEY)
  public abstract FunctionConfig getFunctionConfig();

  @JsonProperty(Keys.QUEUE_CONFIG_KEY)
  public abstract QueueConfig getQueueConfig();

  public interface Builder {

    CreateNodeRequest build();

    Builder setNodeId(String nodeId);

    Builder setFunctionConfig(FunctionConfig config);

    Builder setQueueConfig(QueueConfig config);
  }
}
