package org.cirrus.infrastructure.factory;

import java.util.List;
import software.amazon.awscdk.services.apigatewayv2.AddRoutesOptions;
import software.amazon.awscdk.services.apigatewayv2.HttpApi;
import software.amazon.awscdk.services.apigatewayv2.HttpMethod;
import software.amazon.awscdk.services.apigatewayv2.HttpStageOptions;
import software.amazon.awscdk.services.apigatewayv2.IHttpApi;
import software.amazon.awscdk.services.apigatewayv2.IHttpRouteAuthorizer;
import software.amazon.awscdk.services.apigatewayv2.authorizers.HttpUserPoolAuthorizer;
import software.amazon.awscdk.services.apigatewayv2.integrations.HttpLambdaIntegration;
import software.amazon.awscdk.services.cognito.AccountRecovery;
import software.amazon.awscdk.services.cognito.IUserPool;
import software.amazon.awscdk.services.cognito.Mfa;
import software.amazon.awscdk.services.cognito.PasswordPolicy;
import software.amazon.awscdk.services.cognito.StandardAttribute;
import software.amazon.awscdk.services.cognito.StandardAttributes;
import software.amazon.awscdk.services.cognito.UserPool;
import software.amazon.awscdk.services.lambda.IFunction;
import software.constructs.Construct;

public final class NodeApiFactory {

  private static final String API_NAME = "NodeApi";
  private static final String CREATE_NODE = "CreateNode";
  private static final String DELETE_NODE = "DeleteNode";
  private static final String CREATE_NODE_PATH = "../create-node-handler";
  private static final String DELETE_NODE_PATH = "../delete-node-handler";
  private static final String NODE_ENDPOINT = "/node";
  private static final String DEV_STAGE_ID = "DevStage";
  private static final String DEV_STAGE = "dev";
  private static final String AUTHORIZER_ID = API_NAME + "Authorizer";
  private static final String USER_POOL_ID = API_NAME + "UserPool";

  private NodeApiFactory() {
    // no-op
  }

  public static IHttpApi create(Construct scope) {
    HttpApi api = HttpApi.Builder.create(scope, API_NAME).build();
    addRoutes(api, scope);
    addStages(api);
    return api;
  }

  private static void addRoutes(HttpApi api, Construct scope) {
    api.addRoutes(createNode(scope));
    api.addRoutes(deleteNode(scope));
  }

  private static AddRoutesOptions deleteNode(Construct scope) {
    return routeOptions(DELETE_NODE, DELETE_NODE_PATH, List.of(HttpMethod.DELETE), scope);
  }

  private static AddRoutesOptions createNode(Construct scope) {
    return routeOptions(CREATE_NODE, CREATE_NODE_PATH, List.of(HttpMethod.POST), scope);
  }

  private static AddRoutesOptions routeOptions(
      String handlerName, String codePath, List<HttpMethod> methods, Construct scope) {
    IFunction handler = ApiHandlerFactory.create(handlerName, codePath, scope);
    return AddRoutesOptions.builder()
        .path(NODE_ENDPOINT)
        .methods(methods)
        .integration(new HttpLambdaIntegration(handlerName, handler))
        .authorizer(authorizer(scope))
        .build();
  }

  private static void addStages(HttpApi api) {
    addDevStage(api);
  }

  private static void addDevStage(HttpApi api) {
    api.addStage(DEV_STAGE_ID, HttpStageOptions.builder().stageName(DEV_STAGE).build());
  }

  private static IHttpRouteAuthorizer authorizer(Construct scope) {
    return HttpUserPoolAuthorizer.Builder.create(AUTHORIZER_ID, userPool(scope)).build();
  }

  private static IUserPool userPool(Construct scope) {
    return UserPool.Builder.create(scope, USER_POOL_ID)
        .passwordPolicy(passwordPolicy())
        .standardAttributes(standardAttributes())
        .selfSignUpEnabled(true)
        .mfa(Mfa.REQUIRED)
        .accountRecovery(AccountRecovery.PHONE_WITHOUT_MFA_AND_EMAIL)
        .build();
  }

  private static PasswordPolicy passwordPolicy() {
    return PasswordPolicy.builder()
        .minLength(10)
        .requireDigits(true)
        .requireLowercase(true)
        .requireUppercase(true)
        .requireSymbols(true)
        .build();
  }

  private static StandardAttributes standardAttributes() {
    return StandardAttributes.builder()
        .email(StandardAttribute.builder().required(true).mutable(true).build())
        .phoneNumber(StandardAttribute.builder().required(true).mutable(true).build())
        .build();
  }
}
