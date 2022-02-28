package org.cirrus.infrastructure.factory;

import java.util.List;
import software.amazon.awscdk.services.apigatewayv2.AddRoutesOptions;
import software.amazon.awscdk.services.apigatewayv2.HttpApi;
import software.amazon.awscdk.services.apigatewayv2.HttpMethod;
import software.amazon.awscdk.services.apigatewayv2.IHttpApi;
import software.amazon.awscdk.services.apigatewayv2.integrations.HttpLambdaIntegration;
import software.amazon.awscdk.services.lambda.IFunction;
import software.constructs.Construct;

public final class ApiFactory {

  private static final String API_NAME = "NodeApi";
  private static final String CREATE_NODE = "CreateNode";
  private static final String DELETE_NODE = "DeleteNode";
  private static final String CREATE_NODE_PATH = "../create-node-handler";
  private static final String DELETE_NODE_PATH = "../delete-node-handler";
  private static final String CREATE_ENDPOINT = "/create";
  private static final String DELETE_ENDPOINT = "/delete";

  public static IHttpApi create(Construct scope) {
    HttpApi api = HttpApi.Builder.create(scope, API_NAME).apiName(API_NAME).build();
    api.addRoutes(createNode(scope));
    api.addRoutes(deleteNode(scope));
    return api;
  }

  private static AddRoutesOptions deleteNode(Construct scope) {
    return routeOptions(
        DELETE_NODE, DELETE_NODE_PATH, DELETE_ENDPOINT, List.of(HttpMethod.DELETE), scope);
  }

  private static AddRoutesOptions createNode(Construct scope) {
    return routeOptions(
        CREATE_NODE, CREATE_NODE_PATH, CREATE_ENDPOINT, List.of(HttpMethod.POST), scope);
  }

  private static AddRoutesOptions routeOptions(
      String functionName,
      String codePath,
      String endpoint,
      List<HttpMethod> methods,
      Construct scope) {
    IFunction handler = ApiHandlerFactory.create(functionName, codePath, scope);
    return AddRoutesOptions.builder()
        .path(endpoint)
        .methods(methods)
        .integration(new HttpLambdaIntegration(functionName, handler))
        .build();
  }
}