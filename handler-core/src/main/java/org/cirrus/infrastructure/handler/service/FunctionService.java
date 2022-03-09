package org.cirrus.infrastructure.handler.service;

import java.util.concurrent.CompletableFuture;
import org.cirrus.infrastructure.handler.model.FunctionConfig;
import org.cirrus.infrastructure.handler.model.QueueConfig;
import org.cirrus.infrastructure.handler.model.Resource;

public interface FunctionService {

  CompletableFuture<String> getUploadUrl(String codeKey);

  CompletableFuture<String> publishCode(String codeId, String runtime);

  CompletableFuture<Resource> createFunction(FunctionConfig config);

  CompletableFuture<Void> deleteFunction(String functionId);

  CompletableFuture<String> attachQueue(String functionId, String queueId, QueueConfig config);
}
