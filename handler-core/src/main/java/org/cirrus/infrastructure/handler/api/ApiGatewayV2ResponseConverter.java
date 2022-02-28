package org.cirrus.infrastructure.handler.api;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import org.cirrus.infrastructure.util.Converter;

public class ApiGatewayV2ResponseConverter
    implements Converter<ApiResponse, APIGatewayV2HTTPResponse> {

  @Override
  public APIGatewayV2HTTPResponse forward(ApiResponse response) {
    return APIGatewayV2HTTPResponse.builder()
        .withBody(response.body())
        .withStatusCode(response.status())
        .withIsBase64Encoded(false)
        .build();
  }

  @Override
  public ApiResponse backward(APIGatewayV2HTTPResponse response) {
    return ApiResponse.of(response.getBody(), response.getStatusCode());
  }
}
