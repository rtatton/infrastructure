package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cirrus.infrastructure.handler.model.FunctionConfig;
import org.cirrus.infrastructure.handler.model.QueueConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableCreateNodeRequest.class)
@JsonDeserialize(as = ImmutableCreateNodeRequest.class)
public abstract class CreateNodeRequest {

  public static Builder builder() {
    return ImmutableCreateNodeRequest.builder();
  }

  public abstract FunctionConfig functionConfig();

  public abstract QueueConfig queueConfig();

  public abstract static class Builder {

    public abstract CreateNodeRequest build();

    public abstract Builder functionConfig(FunctionConfig config);

    public abstract Builder queueConfig(QueueConfig config);
  }
}
