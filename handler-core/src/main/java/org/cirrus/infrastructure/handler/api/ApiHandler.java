package org.cirrus.infrastructure.handler.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import org.cirrus.infrastructure.handler.util.Logger;
import org.cirrus.infrastructure.handler.util.Mapper;

public class ApiHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

  private final ApiCommand command;
  private final Mapper mapper;
  private final Logger logger;

  protected ApiHandler(ApiCommand command, Mapper mapper, Logger logger) {
    this.command = command;
    this.mapper = mapper;
    this.logger = logger;
  }

  @Override
  public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
    ApiRequest request = toRequest(event);
    ApiResponse response = command.run(request);
    return mapResponse(response);
  }

  private ApiRequest toRequest(APIGatewayV2HTTPEvent event) {
    logger.debug("Received API request:%n", mapper.write(event));
    return ApiRequest.of(event.getBody());
  }

  private APIGatewayV2HTTPResponse mapResponse(ApiResponse response) {
    APIGatewayV2HTTPResponse mapped =
        APIGatewayV2HTTPResponse.builder()
            .withBody(response.body())
            .withStatusCode(response.status())
            .withIsBase64Encoded(false)
            .build();
    logger.debug("Returning API response:%n", mapper.write(mapped));
    return mapped;
  }
}
