package org.cirrus.infrastructure.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import org.cirrus.infrastructure.handler.api.ApiRequest;
import org.cirrus.infrastructure.handler.api.ApiResponse;

public abstract class AbstractHandler
    implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

  private static ApiRequest mapInput(APIGatewayV2HTTPEvent event) {
    return ApiRequest.of(event.getBody());
  }

  protected abstract ApiResponse handle(ApiRequest request);

  private static APIGatewayV2HTTPResponse mapOutput(ApiResponse response) {
    return APIGatewayV2HTTPResponse.builder()
        .withBody(response.body())
        .withStatusCode(response.status())
        .withIsBase64Encoded(false)
        .build();
  }

  @Override
  public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
    ApiRequest request = mapInput(event);
    ApiResponse response = handle(request);
    return mapOutput(response);
  }
}
