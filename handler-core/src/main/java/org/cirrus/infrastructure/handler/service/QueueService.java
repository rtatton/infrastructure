package org.cirrus.infrastructure.handler.service;

import java.util.concurrent.CompletableFuture;
import org.cirrus.infrastructure.handler.model.QueueConfig;

public interface QueueService {

  CompletableFuture<String> createQueue(QueueConfig config);

  CompletableFuture<Void> deleteQueue(String queueId);
}
