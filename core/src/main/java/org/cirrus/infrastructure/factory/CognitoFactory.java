package org.cirrus.infrastructure.factory;

import org.cirrus.infrastructure.util.Outputs;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.core.RemovalPolicy;
import software.amazon.awscdk.services.cognito.AccountRecovery;
import software.amazon.awscdk.services.cognito.AuthFlow;
import software.amazon.awscdk.services.cognito.AutoVerifiedAttrs;
import software.amazon.awscdk.services.cognito.CognitoDomainOptions;
import software.amazon.awscdk.services.cognito.IUserPool;
import software.amazon.awscdk.services.cognito.IUserPoolClient;
import software.amazon.awscdk.services.cognito.PasswordPolicy;
import software.amazon.awscdk.services.cognito.SignInAliases;
import software.amazon.awscdk.services.cognito.UserPool;
import software.amazon.awscdk.services.cognito.UserPoolClient;
import software.amazon.awscdk.services.cognito.UserPoolDomain;
import software.amazon.awscdk.services.cognito.UserVerificationConfig;

public class CognitoFactory {

  private static final String USER_POOL_ID = "UserPool";
  private static final String CLIENT_ID = USER_POOL_ID + "Client";
  private static final String DOMAIN_ID = USER_POOL_ID + "Domain";
  private static final String DOMAIN_PREFIX = "project-cirrus";
  private static final String USER_POOL_ID_OUTPUT = "userPoolId";
  private static final String CLIENT_ID_OUTPUT = "userPoolClientId";
  private static final String DOMAIN_URL_OUTPUT = "userPoolBaseUrl";

  public static IUserPool userPool(Construct scope) {
    IUserPool userPool =
        UserPool.Builder.create(scope, USER_POOL_ID)
            .passwordPolicy(
                PasswordPolicy.builder()
                    .minLength(16)
                    .requireDigits(true)
                    .requireLowercase(true)
                    .requireUppercase(true)
                    .requireSymbols(true)
                    .build())
            .signInAliases(SignInAliases.builder().username(true).email(true).build())
            .autoVerify(AutoVerifiedAttrs.builder().email(true).build())
            .userVerification(UserVerificationConfig.builder().build())
            .removalPolicy(RemovalPolicy.DESTROY)
            .accountRecovery(AccountRecovery.EMAIL_ONLY)
            .selfSignUpEnabled(true)
            .build();
    return Outputs.output(scope, USER_POOL_ID_OUTPUT, userPool, IUserPool::getUserPoolId);
  }

  public static IUserPoolClient userPoolClient(Construct scope, IUserPool userPool) {
    IUserPoolClient client =
        UserPoolClient.Builder.create(scope, CLIENT_ID)
            .userPool(userPool)
            .authFlows(
                AuthFlow.builder()
                    .adminUserPassword(true)
                    .custom(true)
                    .userPassword(true)
                    .userSrp(true)
                    .build())
            .idTokenValidity(Duration.hours(12))
            .build();
    return Outputs.output(scope, CLIENT_ID_OUTPUT, client, IUserPoolClient::getUserPoolClientId);
  }

  public static void userPoolDomain(Construct scope, IUserPool userPool) {
    UserPoolDomain domain =
        UserPoolDomain.Builder.create(scope, DOMAIN_ID)
            .userPool(userPool)
            .cognitoDomain(CognitoDomainOptions.builder().domainPrefix(DOMAIN_PREFIX).build())
            .build();
    Outputs.output(scope, DOMAIN_URL_OUTPUT, domain, UserPoolDomain::baseUrl);
  }
}
