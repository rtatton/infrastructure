package org.cirrus.infrastructure.handler.service;

import java.util.concurrent.CompletionStage;
import org.cirrus.infrastructure.handler.model.QueueConfig;
import org.cirrus.infrastructure.handler.model.Resource;

public interface QueueService {

  CompletionStage<Resource> createQueue(QueueConfig config);

  CompletionStage<Void> deleteQueue(String queueId);
}
