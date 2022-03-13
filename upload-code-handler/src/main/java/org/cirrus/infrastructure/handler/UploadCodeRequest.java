package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableUploadCodeRequest.class)
@JsonDeserialize(as = ImmutableUploadCodeRequest.class)
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
