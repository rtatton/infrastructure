package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cirrus.infrastructure.util.Keys;
import org.immutables.value.Value;

@Value.Immutable
public interface DeleteNodeRequest {

  static Builder builder() {
    return ImmutableDeleteNodeRequest.builder();
  }

  @JsonProperty(Keys.NODE_KEY)
  String nodeId();

  interface Builder {

    Builder nodeId(String nodeId);

    DeleteNodeRequest build();
  }
}
