package org.cirrus.infrastructure.handler.api;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import org.cirrus.infrastructure.util.Converter;

public class ApiGatewayV2EventConverter implements Converter<APIGatewayV2HTTPEvent, ApiRequest> {

  @Override
  public ApiRequest forward(APIGatewayV2HTTPEvent event) {
    return ApiRequest.of(event.getBody());
  }

  @Override
  public APIGatewayV2HTTPEvent backward(ApiRequest request) {
    return APIGatewayV2HTTPEvent.builder().withBody(request.body()).build();
  }
}
