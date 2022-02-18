package org.cirrus.infrastructure.handler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cirrus.infrastructure.util.Keys;
import org.immutables.value.Value;

@Value.Immutable
public abstract class CreateNodeResponse {

  public static Builder builder() {
    return ImmutableCreateNodeResponse.builder();
  }

  @JsonProperty(Keys.NODE_KEY)
  public abstract String nodeId();

  @JsonProperty(Keys.FUNCTION_KEY)
  public abstract String functionId();

  @JsonProperty(Keys.QUEUE_KEY)
  public abstract String queueId();

  public interface Builder {

    CreateNodeResponse build();

    Builder nodeId(String nodeId);

    Builder functionId(String functionId);

    Builder queueId(String queueId);
  }
}
