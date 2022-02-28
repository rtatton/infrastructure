package org.cirrus.infrastructure.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import org.cirrus.infrastructure.handler.api.ApiCommand;
import org.cirrus.infrastructure.handler.api.ApiRequest;
import org.cirrus.infrastructure.handler.api.ApiResponse;

public class CreateNodeHandler
    implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

  private static final ApiCommand COMMAND = CreateNodeApi.create();

  @Override
  public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
    ApiRequest request = ApiRequest.of(event.getBody());
    ApiResponse response = COMMAND.run(request);
    return APIGatewayV2HTTPResponse.builder()
        .withBody(request.body())
        .withStatusCode(response.status())
        .withIsBase64Encoded(false)
        .build();
  }
}
