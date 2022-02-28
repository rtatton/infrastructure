package org.cirrus.infrastructure.handler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cirrus.infrastructure.util.Keys;
import org.immutables.value.Value;

@Value.Immutable
public interface CreateNodeResponse {

  static Builder builder() {
    return ImmutableCreateNodeResponse.builder();
  }

  @JsonProperty(Keys.NODE_KEY)
  String nodeId();

  @JsonProperty(Keys.FUNCTION_KEY)
  String functionId();

  @JsonProperty(Keys.QUEUE_KEY)
  String queueId();

  interface Builder {

    CreateNodeResponse build();

    Builder nodeId(String nodeId);

    Builder functionId(String functionId);

    Builder queueId(String queueId);
  }
}
