package org.cirrus.infrastructure.factory;

import java.util.List;
import org.immutables.builder.Builder;
import software.amazon.awscdk.services.iam.IRole;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.constructs.Construct;

final class IamFactory {

  private static final String LAMBDA_SERVICE = "lambda.amazonaws.com";
  private static final String ANY = "*";
  private static final String CREATE_FUNCTION = "lambda:CreateFunction";
  private static final String DELETE_FUNCTION = "lambda:DeleteFunction";
  private static final String CREATE_EVENT_SOURCE_MAPPING = "lambda:CreateEventSourceMapping";
  private static final String DELETE_EVENT_SOURCE_MAPPING = "lambda:DeleteEventSourceMapping";
  private static final String CREATE_QUEUE = "sqs:CreateQueue";
  private static final String DELETE_QUEUE = "sqs:DeleteQueue";
  private static final String SEND_MESSAGE = "sqs:SendMessage";
  private static final String GET_QUEUE_URL = "sqs:GetQueueUrl";
  private static final String GET_OBJECT = "s3:GetObject";
  private static final String LAMBDA_SQS_EXECUTION_ROLE = "AWSLambdaSQSQueueExecutionRole";
  private static final String EFS_READ_WRITE_ROLE = "AmazonElasticFileSystemClientReadWriteAccess";
  private static final String NODE_ROLE = "NodeRole";

  private IamFactory() {
    // no-op
  }

  @Builder.Factory
  public static List<PolicyStatement> createNodePolicy() {
    return List.of(
        PolicyStatement.Builder.create()
            .actions(
                List.of(
                    CREATE_FUNCTION,
                    DELETE_FUNCTION,
                    CREATE_EVENT_SOURCE_MAPPING,
                    DELETE_EVENT_SOURCE_MAPPING,
                    CREATE_QUEUE,
                    DELETE_QUEUE,
                    GET_OBJECT))
            .resources(List.of(ANY))
            .principals(List.of(new ServicePrincipal(LAMBDA_SERVICE)))
            .build());
  }

  @Builder.Factory
  public static List<PolicyStatement> deleteNodePolicy() {
    return List.of(
        PolicyStatement.Builder.create()
            .actions(List.of(DELETE_FUNCTION, DELETE_EVENT_SOURCE_MAPPING, DELETE_QUEUE))
            .resources(List.of(ANY))
            .principals(List.of(new ServicePrincipal(LAMBDA_SERVICE)))
            .build());
  }

  @Builder.Factory
  public static IRole nodeRole(@Builder.Parameter Construct scope) {
    Role role = Role.Builder.create(scope, NODE_ROLE).build();
    role.addToPolicy(
        PolicyStatement.Builder.create()
            .actions(List.of(SEND_MESSAGE, GET_QUEUE_URL))
            .resources(List.of(ANY))
            .build());
    role.addManagedPolicy(ManagedPolicy.fromAwsManagedPolicyName(LAMBDA_SQS_EXECUTION_ROLE));
    role.addManagedPolicy(ManagedPolicy.fromAwsManagedPolicyName(EFS_READ_WRITE_ROLE));
    return role;
  }
}
