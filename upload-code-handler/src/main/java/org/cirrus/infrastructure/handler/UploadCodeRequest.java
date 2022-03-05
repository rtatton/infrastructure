package org.cirrus.infrastructure.handler;

import org.immutables.value.Value;

@Value.Immutable
interface UploadCodeRequest {

  static UploadCodeRequest create() {
    return ImmutableUploadCodeRequest.builder().build();
  }

  static Builder builder() {
    return ImmutableUploadCodeRequest.builder();
  }

  interface Builder {

    UploadCodeRequest build();
  }
}
