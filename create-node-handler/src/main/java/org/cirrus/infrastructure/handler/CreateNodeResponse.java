package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cirrus.infrastructure.util.Keys;
import org.immutables.value.Value;

@Value.Immutable
public interface CreateNodeResponse {

  static Builder builder() {
    return ImmutableCreateNodeResponse.builder();
  }

  @JsonProperty(Keys.NODE_ID)
  String nodeId();

  @JsonProperty(Keys.FUNCTION_ID)
  String functionId();

  @JsonProperty(Keys.QUEUE_ID)
  String queueId();

  interface Builder {

    CreateNodeResponse build();

    Builder nodeId(String nodeId);

    Builder functionId(String functionId);

    Builder queueId(String queueId);
  }
}
