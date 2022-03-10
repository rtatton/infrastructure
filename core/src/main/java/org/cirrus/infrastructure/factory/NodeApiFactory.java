package org.cirrus.infrastructure.factory;

import java.util.List;
import org.cirrus.infrastructure.util.Keys;
import org.immutables.builder.Builder;
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
import software.constructs.Construct;

final class NodeApiFactory {

  private static final String API_ID = "NodeApi";
  private static final String UPLOAD_CODE_HANDLER = "UploadCodeHandler";
  private static final String PUBLISH_CODE_HANDLER = "PublishCodeHandler";
  private static final String CREATE_NODE_HANDLER = "CreateNodeHandler";
  private static final String DELETE_NODE_HANDLER = "DeleteNodeHandler";
  private static final String UPLOAD_CODE_PATH = "../upload-code-handler";
  private static final String PUBLISH_CODE_PATH = "../publish-code-handler";
  private static final String CREATE_NODE_PATH = "../create-node-handler";
  private static final String DELETE_NODE_PATH = "../delete-node-handler";
  private static final String NODE_ENDPOINT = "/node";
  private static final String DEV_STAGE_ID = "DevStage";
  private static final String DEV_STAGE = "dev";
  private static final String AUTHORIZER_ID = API_ID + "Authorizer";
  private static final String USER_POOL_ID = API_ID + "UserPool";

  private NodeApiFactory() {
    // no-op
  }

  @Builder.Factory
  public static IHttpApi nodeApi(
      @Builder.Parameter Construct scope, ITable nodeTable, IBucket codeBucket, IRole nodeRole) {
    HttpApi api = HttpApi.Builder.create(scope, API_ID).build();
    addRoutes(scope, api, nodeTable, codeBucket, nodeRole);
    addStages(api);
    addMetrics(api);
    return api;
  }

  private static void addRoutes(
      Construct scope, HttpApi api, ITable nodeTable, IBucket codeBucket, IRole nodeRole) {
    IHttpRouteAuthorizer authorizer = authorizer(scope);
    api.addRoutes(uploadCode(scope, codeBucket, authorizer));
    api.addRoutes(publishCode(scope, authorizer));
    api.addRoutes(createNode(scope, nodeTable, codeBucket, nodeRole, authorizer));
    api.addRoutes(deleteNode(scope, nodeTable, authorizer));
  }

  private static AddRoutesOptions uploadCode(
      Construct scope, IBucket codeBucket, IHttpRouteAuthorizer authorizer) {
    IFunction handler =
        UploadCodeHandlerBuilder.create(scope)
            .apiHandler(UPLOAD_CODE_HANDLER)
            .codePath(UPLOAD_CODE_PATH)
            .region(region())
            .accessKeyId(accessKeyId())
            .secretAccessKey(secretAccessKey())
            .build();
    codeBucket.grantPut(handler);
    return addRouteOptions(handler, UPLOAD_CODE_HANDLER, List.of(HttpMethod.GET), authorizer);
  }

  private static AddRoutesOptions publishCode(Construct scope, IHttpRouteAuthorizer authorizer) {
    IFunction handler =
        PublishCodeHandlerBuilder.create(scope)
            .apiHandler(PUBLISH_CODE_HANDLER)
            .codePath(PUBLISH_CODE_PATH)
            .region(region())
            .accessKeyId(accessKeyId())
            .secretAccessKey(secretAccessKey())
            .build();
    PublishCodePolicyBuilder.create().build().forEach(handler::addToRolePolicy);
    return addRouteOptions(handler, PUBLISH_CODE_HANDLER, List.of(HttpMethod.POST), authorizer);
  }

  private static AddRoutesOptions createNode(
      Construct scope,
      ITable nodeTable,
      IBucket codeBucket,
      IRole nodeRole,
      IHttpRouteAuthorizer authorizer) {
    IFunction handler =
        CreateNodeHandlerBuilder.create(scope)
            .apiHandler(CREATE_NODE_HANDLER)
            .codePath(CREATE_NODE_PATH)
            .region(region())
            .accessKeyId(accessKeyId())
            .secretAccessKey(secretAccessKey())
            .nodeRole(nodeRole.getRoleArn())
            .nodeHandler("") // TODO
            .nodeRuntime("") // TODO
            .nodeRuntimeBucket("") // TODO
            .nodeRuntimeKey("") // TODO
            .build();
    nodeTable.grantWriteData(handler);
    codeBucket.grantRead(handler);
    CreateNodePolicyBuilder.create().build().forEach(handler::addToRolePolicy);
    return addRouteOptions(handler, CREATE_NODE_HANDLER, List.of(HttpMethod.POST), authorizer);
  }

  private static AddRoutesOptions deleteNode(
      Construct scope, ITable nodeTable, IHttpRouteAuthorizer authorizer) {
    IFunction handler =
        DeleteNodeHandlerBuilder.create(scope)
            .apiHandler(DELETE_NODE_HANDLER)
            .codePath(DELETE_NODE_PATH)
            .region(region())
            .accessKeyId(accessKeyId())
            .secretAccessKey(secretAccessKey())
            .build();
    nodeTable.grantWriteData(handler);
    DeleteNodePolicyBuilder.create().build().forEach(handler::addToRolePolicy);
    return addRouteOptions(handler, DELETE_NODE_HANDLER, List.of(HttpMethod.DELETE), authorizer);
  }

  private static String accessKeyId() {
    return System.getenv(Keys.AWS_ACCESS_KEY_ID);
  }

  private static String secretAccessKey() {
    return System.getenv(Keys.AWS_SECRET_ACCESS_KEY);
  }

  private static String region() {
    return System.getenv(Keys.AWS_REGION);
  }

  private static AddRoutesOptions addRouteOptions(
      IFunction handler,
      String handlerName,
      List<HttpMethod> methods,
      IHttpRouteAuthorizer authorizer) {
    return AddRoutesOptions.builder()
        .path(NODE_ENDPOINT)
        .methods(methods)
        .integration(new HttpLambdaIntegration(handlerName, handler))
        .authorizer(authorizer)
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

  private static void addMetrics(HttpApi api) {
    api.metricCount();
    api.metricClientError();
    api.metricLatency();
    api.metricServerError();
    api.metricDataProcessed();
  }
}
