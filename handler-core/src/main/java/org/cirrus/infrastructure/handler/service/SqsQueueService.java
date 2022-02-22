package org.cirrus.infrastructure.handler.service;

import java.util.Map;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.cirrus.infrastructure.handler.exception.FailedResourceDeletionException;
import org.cirrus.infrastructure.handler.model.QueueConfig;
import org.cirrus.infrastructure.handler.model.Resource;
import org.cirrus.infrastructure.util.Resources;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

@Singleton
public class SqsQueueService implements QueueService {

  private static final String TRUE = "true";
  private final SqsAsyncClient sqsClient;
  private final ServiceHelper helper;

  @Inject
  public SqsQueueService(SqsAsyncClient sqsClient, ServiceHelper helper) {
    this.sqsClient = sqsClient;
    this.helper = helper;
  }

  @Override
  public CompletionStage<Resource> create(QueueConfig config) {
    String queueId = Resources.createRandomId();
    Map<QueueAttributeName, String> props = getQueueProps(config);
    CompletionStage<CreateQueueResponse> response =
        sqsClient.createQueue(builder -> builder.queueName(queueId).attributes(props));
    return helper.createResource(response, CreateQueueResponse::queueUrl);
  }

  @Override
  public CompletionStage<Void> delete(String queueId) {
    return helper
        .wrapThrowable(
            sqsClient.deleteQueue(builder -> builder.queueUrl(queueId)),
            FailedResourceDeletionException::new)
        .thenApplyAsync(x -> null);
  }

  private Map<QueueAttributeName, String> getQueueProps(QueueConfig config) {
    return Map.of(
        QueueAttributeName.DELAY_SECONDS,
        String.valueOf(config.delaySeconds()),
        QueueAttributeName.MAXIMUM_MESSAGE_SIZE,
        String.valueOf(config.maxMessageSizeBytes()),
        QueueAttributeName.MESSAGE_RETENTION_PERIOD,
        String.valueOf(config.messageRetentionPeriodSeconds()),
        QueueAttributeName.POLICY,
        "", // TODO
        QueueAttributeName.RECEIVE_MESSAGE_WAIT_TIME_SECONDS,
        String.valueOf(config.receiveMessageWaitTimeSeconds()),
        QueueAttributeName.VISIBILITY_TIMEOUT,
        String.valueOf(config.visibilityTimeoutSeconds()),
        QueueAttributeName.FIFO_QUEUE,
        TRUE,
        QueueAttributeName.CONTENT_BASED_DEDUPLICATION,
        TRUE);
  }
}
