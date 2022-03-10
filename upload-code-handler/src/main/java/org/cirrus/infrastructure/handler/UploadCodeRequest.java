package org.cirrus.infrastructure.handler;

import org.immutables.value.Value;

@Value.Immutable
public abstract class UploadCodeRequest {

  public static UploadCodeRequest create() {
    return ImmutableUploadCodeRequest.builder().build();
  }

  public static Builder builder() {
    return ImmutableUploadCodeRequest.builder();
  }

  public interface Builder {

    UploadCodeRequest build();
  }
}
