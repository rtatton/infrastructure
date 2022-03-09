package org.cirrus.infrastructure.handler.service;

import java.util.concurrent.CompletableFuture;
import org.cirrus.infrastructure.handler.model.QueueConfig;
import org.cirrus.infrastructure.handler.model.Resource;

public interface QueueService {

  CompletableFuture<Resource> create(QueueConfig config);

  CompletableFuture<Void> delete(String queueId);
}
