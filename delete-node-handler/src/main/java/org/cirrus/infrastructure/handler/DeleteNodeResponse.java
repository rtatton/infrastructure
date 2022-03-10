package org.cirrus.infrastructure.handler;

import org.immutables.value.Value;

@Value.Immutable
public abstract class DeleteNodeResponse {

  public static DeleteNodeResponse create() {
    return builder().build();
  }

  public static Builder builder() {
    return ImmutableDeleteNodeResponse.builder();
  }

  public interface Builder {

    DeleteNodeResponse build();
  }
}
