package org.cirrus.infrastructure.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.cirrus.infrastructure.handler.util.Mapper;
import org.junit.jupiter.api.Test;

public abstract class ApiModelTests<Request, Response> {

  private final Mapper mapper;
  private final Class<Request> requestType;
  private final Class<Response> responseType;

  protected ApiModelTests(Mapper mapper, Class<Request> requestType, Class<Response> responseType) {
    this.mapper = mapper;
    this.requestType = requestType;
    this.responseType = responseType;
  }

  @Test
  public final void doesNotThrowExceptionWhenWritingRequest() {
    assertDoesNotThrow(this::writeRequest);
  }

  @Test
  public final void doesNotThrowExceptionWhenReadingRequest() {
    assertDoesNotThrow(this::readRequest);
  }

  @Test
  public final void doesNotThrowExceptionWhenWritingResponse() {
    assertDoesNotThrow(this::writeResponse);
  }

  @Test
  public final void doesNotThrowExceptionWhenReadingResponse() {
    assertDoesNotThrow(this::readResponse);
  }

  protected abstract Request request();

  protected abstract Response response();

  private Request readRequest() {
    return mapper.read(writeRequest(), requestType);
  }

  private Response readResponse() {
    return mapper.read(writeResponse(), responseType);
  }

  private String writeRequest() {
    return mapper.write(request());
  }

  private String writeResponse() {
    return mapper.write(response());
  }
}
