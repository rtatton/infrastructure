package org.cirrus.infrastructure.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;

public class CreateNodeHandler implements RequestHandler<APIGatewayV2HTTPEvent, String> {

  private static final Command<?, ?> COMMAND = CreateNodeCommand.getInstance();

  @Override
  public String handleRequest(APIGatewayV2HTTPEvent event, Context context) {
    return COMMAND.runFromString(event.getBody());
  }
}
