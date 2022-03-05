package org.cirrus.infrastructure.handler;

import org.immutables.value.Value;

@Value.Immutable
public interface DeleteNodeResponse {

  static DeleteNodeResponse create() {
    return builder().build();
  }

  static Builder builder() {
    return ImmutableDeleteNodeResponse.builder();
  }

  interface Builder {

    DeleteNodeResponse build();
  }
}
