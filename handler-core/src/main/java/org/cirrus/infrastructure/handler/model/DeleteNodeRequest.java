package org.cirrus.infrastructure.handler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cirrus.infrastructure.util.Keys;
import org.immutables.value.Value;

@Value.Immutable
public interface DeleteNodeRequest {

  static Builder builder() {
    return ImmutableDeleteNodeRequest.builder();
  }

  @JsonProperty(Keys.NODE_KEY)
  public abstract String nodeId();

  public interface Builder {
    Builder nodeId(String nodeId);

    DeleteNodeRequest build();
  }
}
