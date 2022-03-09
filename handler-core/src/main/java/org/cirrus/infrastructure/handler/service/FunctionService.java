package org.cirrus.infrastructure.handler.service;

import java.util.concurrent.CompletionStage;
import org.cirrus.infrastructure.handler.model.FunctionConfig;
import org.cirrus.infrastructure.handler.model.QueueConfig;
import org.cirrus.infrastructure.handler.model.Resource;

public interface FunctionService {

  CompletionStage<String> getUploadUrl(String codeKey);

  CompletionStage<String> publishCode(String codeId, String runtime);

  CompletionStage<Resource> create(FunctionConfig config);

  CompletionStage<Void> delete(String functionId);

  CompletionStage<String> attachQueue(String functionId, String queueId, QueueConfig config);
}
