package org.cirrus.infrastructure.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;

public class DeleteNodeHandler implements RequestHandler<APIGatewayV2HTTPEvent, Void> {

  private static final Command<?, ?> COMMAND = DeleteNodeCommand.getInstance();

  @Override
  public Void handleRequest(APIGatewayV2HTTPEvent event, Context context) {
    COMMAND.runFromString(event.getBody());
    // TODO How does API Gateway map null response? Be consistent and just return empty string?
    return null;
  }
}
