package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableDeleteNodeResponse.class)
@JsonDeserialize(as = ImmutableDeleteNodeResponse.class)
public abstract class DeleteNodeResponse {

  public static DeleteNodeResponse create() {
    return builder().build();
  }

  public static Builder builder() {
    return ImmutableDeleteNodeResponse.builder();
  }

  public abstract static class Builder {

    public abstract DeleteNodeResponse build();
  }
}
