package org.cirrus.infrastructure.handler.service;

import java.util.concurrent.CompletionStage;
import org.cirrus.infrastructure.handler.model.FunctionConfig;
import org.cirrus.infrastructure.handler.model.QueueConfig;
import org.cirrus.infrastructure.handler.model.Resource;

public interface FunctionService {

  CompletionStage<Resource> createFunction(FunctionConfig config);

  CompletionStage<Void> deleteFunction(String functionId);

  CompletionStage<String> attachQueue(String functionId, String queueId, QueueConfig config);
}
