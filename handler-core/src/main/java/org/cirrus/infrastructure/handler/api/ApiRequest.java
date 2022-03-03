package org.cirrus.infrastructure.handler.api;

import org.immutables.value.Value;

@Value.Immutable
public interface ApiRequest {

  static Builder builder() {
    return ImmutableApiRequest.builder();
  }

  static ApiRequest of(String body) {
    return ImmutableApiRequest.of(body);
  }

  String body();

  interface Builder {

    Builder body(String body);

    ApiRequest build();
  }
}
