package org.cirrus.infrastructure.handler.api;

import org.immutables.value.Value;

@Value.Immutable
public interface ApiResponse {

  static Builder builder() {
    return ImmutableApiResponse.builder();
  }

  static ApiResponse of(String body, int status) {
    return ImmutableApiResponse.of(body, status);
  }

  String body();

  int status();

  interface Builder {

    Builder body(String body);

    Builder status(int status);

    ApiResponse build();
  }
}
