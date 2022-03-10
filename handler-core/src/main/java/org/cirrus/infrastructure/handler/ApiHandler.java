package org.cirrus.infrastructure.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import org.cirrus.infrastructure.handler.api.ApiRequest;
import org.cirrus.infrastructure.handler.api.ApiResponse;

public abstract class ApiHandler
    implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

  @Override
  public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
    ApiRequest request = ApiRequest.of(event.getBody());
    ApiResponse response = handle(request);
    return APIGatewayV2HTTPResponse.builder()
        .withBody(response.body())
        .withStatusCode(response.status())
        .withIsBase64Encoded(false)
        .build();
  }

  protected abstract ApiResponse handle(ApiRequest request);
}
