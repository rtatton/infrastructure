package org.cirrus.infrastructure.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;

public class DeleteNodeHandler
    implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

  private static final Command<?, ?> COMMAND = DeleteNodeCommand.getInstance();

  @Override
  public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
    return APIGatewayV2HTTPResponse.builder()
        .withBody(COMMAND.runFromString(event.getBody()))
        .withStatusCode(200)
        .withIsBase64Encoded(false)
        .build();
  }
}
