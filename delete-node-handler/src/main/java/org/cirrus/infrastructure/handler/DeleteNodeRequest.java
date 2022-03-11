package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cirrus.infrastructure.util.Keys;
import org.cirrus.infrastructure.util.Preconditions;
import org.immutables.value.Value;

@Value.Immutable
public abstract class DeleteNodeRequest {

  public static Builder builder() {
    return ImmutableDeleteNodeRequest.builder();
  }

  @JsonProperty(Keys.NODE_ID)
  public abstract String nodeId();

  @Value.Check
  protected void check() {
    Preconditions.checkNotNullOrEmpty(nodeId());
  }

  public interface Builder {

    Builder nodeId(String nodeId);

    DeleteNodeRequest build();
  }
}
