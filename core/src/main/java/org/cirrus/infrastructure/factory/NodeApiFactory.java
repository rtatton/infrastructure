package org.cirrus.infrastructure.factory;

import java.util.List;
import org.immutables.builder.Builder;
import software.amazon.awscdk.core.Construct;
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
import software.amazon.awscdk.services.dynamodb.ITable;
import software.amazon.awscdk.services.iam.IRole;
import software.amazon.awscdk.services.lambda.IFunction;
import software.amazon.awscdk.services.s3.IBucket;

final class NodeApiFactory {

  private static final String API_ID = "NodeApi";
  private static final String UPLOAD_CODE_HANDLER = "UploadCodeHandler";
  private static final String PUBLISH_CODE_HANDLER = "PublishCodeHandler";
  private static final String CREATE_NODE_HANDLER = "CreateNodeHandler";
  private static final String DELETE_NODE_HANDLER = "DeleteNodeHandler";
  private static final String NODE_ENDPOINT = "/node";
  private static final String CODE_ENDPOINT = "/code";
  private static final String DEV_STAGE_ID = "DevStage";
  private static final String DEV_STAGE = "dev";
  private static final String AUTHORIZER_ID = API_ID + "Authorizer";
  private static final String USER_POOL_ID = API_ID + "UserPool";

  private NodeApiFactory() {
    // no-op
  }

  @Builder.Factory
  public static IHttpApi nodeApi(
      @Builder.Parameter Construct scope,
      ITable nodeTable,
      IBucket runtimeBucket,
      IBucket uploadBucket,
      IRole nodeRole) {
    HttpApi api = newApi(scope);
    addRoutes(scope, api, nodeTable, runtimeBucket, uploadBucket, nodeRole);
    addStages(api);
    addMetrics(api);
    return api;
  }

  private static HttpApi newApi(Construct scope) {
    return HttpApi.Builder.create(scope, API_ID).defaultAuthorizer(authorizer(scope)).build();
  }

  private static void addRoutes(
      Construct scope,
      HttpApi api,
      ITable nodeTable,
      IBucket runtimeBucket,
      IBucket uploadBucket,
      IRole nodeRole) {
    api.addRoutes(uploadCode(scope, uploadBucket));
    api.addRoutes(publishCode(scope));
    api.addRoutes(createNode(scope, nodeTable, runtimeBucket, uploadBucket, nodeRole));
    api.addRoutes(deleteNode(scope, nodeTable));
  }

  private static AddRoutesOptions uploadCode(Construct scope, IBucket uploadBucket) {
    IFunction handler = ApiHandlerFactory.uploadCodeHandler(scope, uploadBucket.getBucketName());
    uploadBucket.grantPut(handler);
    return addCodeRouteOptions(handler, UPLOAD_CODE_HANDLER, List.of(HttpMethod.GET));
  }

  private static AddRoutesOptions publishCode(Construct scope) {
    IFunction handler = ApiHandlerFactory.publishCodeHandler(scope);
    IamFactory.publishCodePolicy().forEach(handler::addToRolePolicy);
    return addCodeRouteOptions(handler, PUBLISH_CODE_HANDLER, List.of(HttpMethod.POST));
  }

  private static AddRoutesOptions createNode(
      Construct scope,
      ITable nodeTable,
      IBucket runtimeBucket,
      IBucket uploadBucket,
      IRole nodeRole) {
    IFunction handler =
        ApiHandlerFactory.createNodeHandler(
            scope, nodeRole.getRoleArn(), runtimeBucket.getBucketName());
    nodeTable.grantWriteData(handler);
    uploadBucket.grantRead(handler);
    IamFactory.createNodePolicy().forEach(handler::addToRolePolicy);
    return addNodeRouteOptions(handler, CREATE_NODE_HANDLER, List.of(HttpMethod.POST));
  }

  private static AddRoutesOptions deleteNode(Construct scope, ITable nodeTable) {
    IFunction handler = ApiHandlerFactory.deleteNodeHandler(scope);
    nodeTable.grantWriteData(handler);
    IamFactory.deleteNodePolicy().forEach(handler::addToRolePolicy);
    return addNodeRouteOptions(handler, DELETE_NODE_HANDLER, List.of(HttpMethod.DELETE));
  }

  private static AddRoutesOptions addCodeRouteOptions(
      IFunction handler, String handlerName, List<HttpMethod> methods) {
    return addRouteOptions(handler, handlerName, CODE_ENDPOINT, methods);
  }

  private static AddRoutesOptions addNodeRouteOptions(
      IFunction handler, String handlerName, List<HttpMethod> methods) {
    return addRouteOptions(handler, handlerName, NODE_ENDPOINT, methods);
  }

  private static AddRoutesOptions addRouteOptions(
      IFunction handler, String handlerName, String endpoint, List<HttpMethod> methods) {
    return AddRoutesOptions.builder()
        .path(endpoint)
        .methods(methods)
        .integration(new HttpLambdaIntegration(handlerName, handler))
        .build();
  }

  private static void addStages(HttpApi api) {
    api.addStage(DEV_STAGE_ID, HttpStageOptions.builder().stageName(DEV_STAGE).build());
  }

  private static IHttpRouteAuthorizer authorizer(Construct scope) {
    return HttpUserPoolAuthorizer.Builder.create(AUTHORIZER_ID, userPool(scope)).build();
  }

  private static IUserPool userPool(Construct scope) {
    return UserPool.Builder.create(scope, USER_POOL_ID)
        .passwordPolicy(
            PasswordPolicy.builder()
                .minLength(10)
                .requireDigits(true)
                .requireLowercase(true)
                .requireUppercase(true)
                .requireSymbols(true)
                .build())
        .standardAttributes(
            StandardAttributes.builder()
                .email(StandardAttribute.builder().required(true).mutable(true).build())
                .phoneNumber(StandardAttribute.builder().required(true).mutable(true).build())
                .build())
        .selfSignUpEnabled(true)
        .mfa(Mfa.REQUIRED)
        .accountRecovery(AccountRecovery.PHONE_WITHOUT_MFA_AND_EMAIL)
        .build();
  }

  private static void addMetrics(HttpApi api) {
    api.metricCount();
    api.metricClientError();
    api.metricLatency();
    api.metricServerError();
    api.metricDataProcessed();
  }
}
