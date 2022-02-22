package org.cirrus.infrastructure.handler.service;

import java.util.concurrent.CompletionStage;
import org.cirrus.infrastructure.handler.model.QueueConfig;
import org.cirrus.infrastructure.handler.model.Resource;

public interface QueueService {

  CompletionStage<Resource> create(QueueConfig config);

  CompletionStage<Void> delete(String queueId);
}
