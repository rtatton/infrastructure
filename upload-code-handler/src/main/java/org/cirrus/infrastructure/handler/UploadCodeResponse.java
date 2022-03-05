package org.cirrus.infrastructure.handler;

import org.immutables.value.Value;

@Value.Immutable
interface UploadCodeResponse {

  static UploadCodeResponse of(String url) {
    return ImmutableUploadCodeResponse.of(url);
  }

  static Builder builder() {
    return ImmutableUploadCodeResponse.builder();
  }

  String url();

  interface Builder {

    Builder url(String url);

    UploadCodeResponse build();
  }
}
