package org.cirrus.infrastructure.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import org.cirrus.infrastructure.handler.api.ApiGatewayV2EventConverter;
import org.cirrus.infrastructure.handler.api.ApiGatewayV2ResponseConverter;
import org.cirrus.infrastructure.handler.api.ApiRequest;
import org.cirrus.infrastructure.handler.api.ApiResponse;
import org.cirrus.infrastructure.util.Converter;

public abstract class AbstractHandler
    implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

  private static final Converter<APIGatewayV2HTTPEvent, ApiRequest> EVENT_CONVERTER =
      new ApiGatewayV2EventConverter();
  private static final Converter<ApiResponse, APIGatewayV2HTTPResponse> RESPONSE_CONVERTER =
      new ApiGatewayV2ResponseConverter();

  @Override
  public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
    ApiRequest request = EVENT_CONVERTER.forward(event);
    ApiResponse response = handle(request);
    return RESPONSE_CONVERTER.forward(response);
  }

  protected abstract ApiResponse handle(ApiRequest request);
}
