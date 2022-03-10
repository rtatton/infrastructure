package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cirrus.infrastructure.handler.model.FunctionConfig;
import org.cirrus.infrastructure.handler.model.QueueConfig;
import org.cirrus.infrastructure.util.Keys;
import org.cirrus.infrastructure.util.Preconditions;
import org.immutables.value.Value;

@Value.Immutable
public abstract class CreateNodeRequest {

  public static Builder builder() {
    return ImmutableCreateNodeRequest.builder();
  }

  @JsonProperty(Keys.NODE_ID)
  public abstract String nodeId();

  @JsonProperty(Keys.FUNCTION_CONFIG)
  public abstract FunctionConfig functionConfig();

  @JsonProperty(Keys.QUEUE_CONFIG)
  public abstract QueueConfig queueConfig();

  @Value.Check
  protected void check() {
    Preconditions.notNullOrEmpty(nodeId());
  }

  public interface Builder {

    CreateNodeRequest build();

    Builder nodeId(String nodeId);

    Builder functionConfig(FunctionConfig config);

    Builder queueConfig(QueueConfig config);
  }
}
