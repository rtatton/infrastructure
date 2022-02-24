package org.cirrus.infrastructure.handler.fixtures;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.cirrus.infrastructure.handler.exception.FailedEventSourceMappingException;
import org.cirrus.infrastructure.handler.exception.FailedResourceCreationException;
import org.cirrus.infrastructure.handler.exception.FailedResourceDeletionException;
import org.cirrus.infrastructure.handler.exception.FailedStorageReadException;
import org.cirrus.infrastructure.handler.exception.FailedStorageWriteException;
import org.cirrus.infrastructure.handler.exception.NoSuchNodeException;
import org.cirrus.infrastructure.handler.exception.NodeAlreadyExistsException;
import org.cirrus.infrastructure.handler.model.CreateNodeResponse;
import org.cirrus.infrastructure.handler.model.NodeRecord;
import org.cirrus.infrastructure.handler.model.Resource;

public final class HandlerTests {

  public static final String NODE_ID = "nodeId";
  public static final String FUNCTION_ID = "functionId";
  public static final String QUEUE_ID = "queueId";
  public static final String EVENT_SOURCE_MAPPING_ID = "eventSourceMappingId";

  private HandlerTests() {
    // no-op
  }

  public static CreateNodeResponse createNodeResponse() {
    return CreateNodeResponse.builder()
        .nodeId(NODE_ID)
        .functionId(FUNCTION_ID)
        .queueId(QUEUE_ID)
        .build();
  }

  public static CompletionStage<NodeRecord> nodeRecordStage() {
    return CompletableFuture.completedFuture(nodeRecord());
  }

  public static NodeRecord nodeRecord() {
    return NodeRecord.builder().nodeId(NODE_ID).functionId(FUNCTION_ID).queueId(QUEUE_ID).build();
  }

  public static <T> CompletionStage<T> noReturn() {
    return CompletableFuture.completedFuture(null);
  }

  public static CompletionStage<Resource> function() {
    return CompletableFuture.completedFuture(Resource.builder().id(FUNCTION_ID).build());
  }

  public static CompletionStage<Resource> queue() {
    return CompletableFuture.completedFuture(Resource.builder().id(QUEUE_ID).build());
  }

  public static CompletionStage<Resource> failedResource() {
    Resource failed = Resource.builder().exception(new FailedResourceCreationException()).build();
    return CompletableFuture.completedFuture(failed);
  }

  public static CompletionStage<String> eventSourceMappingId() {
    return CompletableFuture.completedFuture(EVENT_SOURCE_MAPPING_ID);
  }

  public static <T> CompletionStage<T> failedStorageWrite() {
    return CompletableFuture.failedFuture(new FailedStorageWriteException());
  }

  public static <T> CompletionStage<T> nodeAlreadyExists() {
    return CompletableFuture.failedFuture(new NodeAlreadyExistsException());
  }

  public static <T> CompletionStage<T> noSuchNode() {
    return CompletableFuture.failedFuture(new NoSuchNodeException());
  }

  public static <T> CompletionStage<T> failedStorageRead() {
    return CompletableFuture.failedFuture(new FailedStorageReadException());
  }

  public static <T> CompletionStage<T> failedResourceDeletion() {
    return CompletableFuture.failedFuture(new FailedResourceDeletionException());
  }

  public static <T> CompletionStage<T> failedEventSourceMapping() {
    return CompletableFuture.failedFuture(new FailedEventSourceMappingException());
  }
}
