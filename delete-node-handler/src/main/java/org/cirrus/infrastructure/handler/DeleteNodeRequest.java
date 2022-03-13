package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cirrus.infrastructure.util.Keys;
import org.cirrus.infrastructure.util.Preconditions;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableDeleteNodeRequest.class)
@JsonDeserialize(as = ImmutableDeleteNodeRequest.class)
public abstract class DeleteNodeRequest {

  public static Builder builder() {
    return ImmutableDeleteNodeRequest.builder();
  }

  public static DeleteNodeRequest of(String nodeId) {
    return ImmutableDeleteNodeRequest.of(nodeId);
  }

  @Value.Parameter
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
