package org.cirrus.infrastructure.handler.api;

import org.cirrus.infrastructure.util.Preconditions;
import org.immutables.value.Value;

@Value.Immutable
public abstract class ApiRequest {

  public static Builder builder() {
    return ImmutableApiRequest.builder();
  }

  public static ApiRequest of(String body) {
    return ImmutableApiRequest.of(body);
  }

  @Value.Parameter
  public abstract String body();

  @Value.Check
  protected void check() {
    Preconditions.checkNotNullOrEmpty(body());
  }

  public interface Builder {

    Builder body(String body);

    ApiRequest build();
  }
}
