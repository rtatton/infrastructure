package org.cirrus.infrastructure.factory;

import java.util.Arrays;
import java.util.List;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.services.iam.IPrincipal;
import software.amazon.awscdk.services.iam.IRole;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;

public final class IamFactory {

  private static final String LAMBDA_SERVICE = "lambda.amazonaws.com";
  private static final String ANY = "*";
  private static final String PUBLISH_LAYER = "lambda:PublishLayerVersion";
  private static final String CREATE_FUNCTION = "lambda:CreateFunction";
  private static final String DELETE_FUNCTION = "lambda:DeleteFunction";
  private static final String CREATE_EVENT_SOURCE_MAPPING = "lambda:CreateEventSourceMapping";
  private static final String DELETE_EVENT_SOURCE_MAPPING = "lambda:DeleteEventSourceMapping";
  private static final String CREATE_QUEUE = "sqs:CreateQueue";
  private static final String DELETE_QUEUE = "sqs:DeleteQueue";
  private static final String SEND_MESSAGE = "sqs:SendMessage";
  private static final String GET_QUEUE_URL = "sqs:GetQueueUrl";
  private static final String LAMBDA_SQS_EXECUTION_ROLE = "AWSLambdaSQSQueueExecutionRole";
  private static final String NODE_ROLE = "NodeRole";

  private IamFactory() {
    // no-op
  }

  public static PolicyStatement publishCodePolicy() {
    return policyStatement(PUBLISH_LAYER);
  }

  public static PolicyStatement createNodePolicy() {
    return policyStatement(
        CREATE_FUNCTION,
        DELETE_FUNCTION,
        CREATE_EVENT_SOURCE_MAPPING,
        DELETE_EVENT_SOURCE_MAPPING,
        CREATE_QUEUE,
        DELETE_QUEUE);
  }

  public static PolicyStatement deleteNodePolicy() {
    return policyStatement(DELETE_FUNCTION, DELETE_EVENT_SOURCE_MAPPING, DELETE_QUEUE);
  }

  public static IRole nodeRole(Construct scope) {
    Role role = Role.Builder.create(scope, NODE_ROLE).assumedBy(lambdaPrincipal()).build();
    role.addToPolicy(policyStatement(SEND_MESSAGE, GET_QUEUE_URL));
    role.addManagedPolicy(ManagedPolicy.fromAwsManagedPolicyName(LAMBDA_SQS_EXECUTION_ROLE));
    return role;
  }

  private static PolicyStatement policyStatement(String... actions) {
    return PolicyStatement.Builder.create()
        .actions(Arrays.asList(actions))
        .resources(any())
        .build();
  }

  private static IPrincipal lambdaPrincipal() {
    return new ServicePrincipal(LAMBDA_SERVICE);
  }

  private static List<String> any() {
    return List.of(ANY);
  }
}
