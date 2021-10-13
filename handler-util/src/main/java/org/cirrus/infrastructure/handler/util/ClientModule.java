package org.cirrus.infrastructure.handler.util;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Module
public class ClientModule {

  private static final Region REGION = Region.US_EAST_2;
  private static final String ACCESS_KEY = "";
  private static final String SECRETE_KEY = "";
  private static final AwsCredentials CREDENTIALS =
      AwsBasicCredentials.create(ACCESS_KEY, SECRETE_KEY);
  private static final AwsCredentialsProvider CREDENTIALS_PROVIDER =
      StaticCredentialsProvider.create(CREDENTIALS);

  @Provides
  @Singleton
  public static LambdaAsyncClient provideLambdaClient() {
    return LambdaAsyncClient.builder()
        .region(REGION)
        .credentialsProvider(CREDENTIALS_PROVIDER)
        .build();
  }

  @Provides
  @Singleton
  public static SqsAsyncClient provideSqsClient() {
    return SqsAsyncClient.builder()
        .region(REGION)
        .credentialsProvider(CREDENTIALS_PROVIDER)
        .build();
  }

  @Provides
  @Singleton
  public static SnsAsyncClient provideSnsClient() {
    return SnsAsyncClient.builder()
        .region(REGION)
        .credentialsProvider(CREDENTIALS_PROVIDER)
        .build();
  }
}
