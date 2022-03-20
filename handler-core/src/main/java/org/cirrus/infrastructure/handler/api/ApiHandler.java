package org.cirrus.infrastructure.handler.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;

public abstract class ApiHandler
    implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse>, ApiCommand {

  @Override
  public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
    ApiRequest request = ApiRequest.of(event.getBody());
    ApiResponse response = run(request);
    return APIGatewayV2HTTPResponse.builder()
        .withBody(response.body())
        .withStatusCode(response.status())
        .withIsBase64Encoded(false)
        .build();
  }
}
