package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cirrus.infrastructure.handler.model.FunctionConfig;
import org.cirrus.infrastructure.handler.model.QueueConfig;
import org.cirrus.infrastructure.util.Keys;
import org.immutables.value.Value;

@Value.Immutable
public interface CreateNodeRequest {

  static Builder builder() {
    return ImmutableCreateNodeRequest.builder();
  }

  @JsonProperty(Keys.NODE_KEY)
  String nodeId();

  @JsonProperty(Keys.FUNCTION_CONFIG_KEY)
  FunctionConfig functionConfig();

  @JsonProperty(Keys.QUEUE_CONFIG_KEY)
  QueueConfig queueConfig();

  interface Builder {

    CreateNodeRequest build();

    Builder nodeId(String nodeId);

    Builder functionConfig(FunctionConfig config);

    Builder queueConfig(QueueConfig config);
  }
}
