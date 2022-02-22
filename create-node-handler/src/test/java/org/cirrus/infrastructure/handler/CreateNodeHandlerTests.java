package org.cirrus.infrastructure.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.cirrus.infrastructure.handler.exception.FailedEventSourceMappingException;
import org.cirrus.infrastructure.handler.exception.FailedResourceCreationException;
import org.cirrus.infrastructure.handler.exception.FailedResourceDeletionException;
import org.cirrus.infrastructure.handler.exception.FailedStorageReadException;
import org.cirrus.infrastructure.handler.exception.FailedStorageWriteException;
import org.cirrus.infrastructure.handler.exception.NodeAlreadyExistsException;
import org.cirrus.infrastructure.handler.model.CreateNodeRequest;
import org.cirrus.infrastructure.handler.model.CreateNodeResponse;
import org.cirrus.infrastructure.handler.model.FunctionConfig;
import org.cirrus.infrastructure.handler.model.NodeRecord;
import org.cirrus.infrastructure.handler.model.QueueConfig;
import org.cirrus.infrastructure.handler.model.Resource;
import org.cirrus.infrastructure.handler.service.FunctionService;
import org.cirrus.infrastructure.handler.service.QueueService;
import org.cirrus.infrastructure.handler.service.StorageService;
import org.cirrus.infrastructure.util.Mapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class CreateNodeHandlerTests {

  private static final String NODE_ID = "nodeId";
  private static final String FUNCTION_ID = "functionId";
  private static final String QUEUE_ID = "queueId";
  private static final String EVENT_SOURCE_MAPPING_ID = "eventSourceMappingId";
  @Mock private FunctionService functionService;
  @Mock private QueueService queueService;
  @Mock private StorageService<NodeRecord> storageService;
  @Mock private Mapper mapper;
  @Mock private FunctionConfig functionConfig;
  @Mock private QueueConfig queueConfig;
  @Mock private CreateNodeRequest request;

  @InjectMocks private CreateNodeCommand command;

  @Test
  public void returnsResponseWhenSuccessful() {
    mockRequest();
    mockSuccessfulGetRecord();
    mockSuccessfulCreateFunction();
    mockSuccessfulCreateQueue();
    mockSuccessfulAttachQueue();
    mockSuccessfulPutRecord();
    assertEquals(response(), run());
  }

  @Test
  public void throwsExceptionWhenGetRecordFails() {
    mockGetRequestNodeId();
    mockFailedGetRecord();
    assertThrows(FailedStorageReadException.class, runCommand());
  }

  @Test
  public void throwsExceptionWhenNodeAlreadyExists() {
    mockGetRequestNodeId();
    mockExistingNodeRecord();
    assertThrows(NodeAlreadyExistsException.class, runCommand());
  }

  @Test
  public void throwsExceptionWhenCreateFunctionFails() {
    mockRequest();
    mockSuccessfulGetRecord();
    mockFailedCreateFunction();
    mockSuccessfulCreateQueue();
    mockSuccessfulDeleteQueue();
    assertThrows(FailedResourceCreationException.class, runCommand());
  }

  @Test
  public void throwsExceptionWhenCreateFunctionAndDeleteQueueFails() {
    mockRequest();
    mockSuccessfulGetRecord();
    mockFailedCreateFunction();
    mockSuccessfulCreateQueue();
    mockFailedDeleteQueue();
    assertThrows(FailedResourceDeletionException.class, runCommand());
  }

  @Test
  public void throwsExceptionWhenCreateQueueFails() {
    mockRequest();
    mockSuccessfulGetRecord();
    mockSuccessfulCreateFunction();
    mockFailedCreateQueue();
    mockSuccessfulDeleteFunction();
    assertThrows(FailedResourceCreationException.class, runCommand());
  }

  @Test
  public void throwsExceptionWhenCreateQueueAndDeleteFunctionFails() {
    mockRequest();
    mockSuccessfulGetRecord();
    mockSuccessfulCreateFunction();
    mockFailedCreateQueue();
    mockFailedDeleteFunction();
    assertThrows(FailedResourceDeletionException.class, runCommand());
  }

  @Test
  public void throwsExceptionWhenCreateFunctionAndCreateQueueFails() {
    mockRequest();
    mockSuccessfulGetRecord();
    mockFailedCreateFunction();
    mockFailedCreateQueue();
    assertThrows(FailedResourceCreationException.class, runCommand());
  }

  @Test
  public void throwsExceptionWhenAttachQueueFails() {
    mockRequest();
    mockSuccessfulGetRecord();
    mockSuccessfulCreateFunction();
    mockSuccessfulCreateQueue();
    mockFailedAttachQueue();
    mockSuccessfulDeleteFunction();
    mockSuccessfulDeleteQueue();
    assertThrows(FailedEventSourceMappingException.class, runCommand());
  }

  @Test
  public void throwsExceptionWhenAttachQueueAndDeleteFunctionFails() {
    mockRequest();
    mockSuccessfulGetRecord();
    mockSuccessfulCreateFunction();
    mockSuccessfulCreateQueue();
    mockFailedAttachQueue();
    mockFailedDeleteFunction();
    mockSuccessfulDeleteQueue();
    assertThrows(FailedResourceDeletionException.class, runCommand());
  }

  @Test
  public void throwsExceptionWhenAttachQueueAndDeleteQueueFails() {
    mockRequest();
    mockSuccessfulGetRecord();
    mockSuccessfulCreateFunction();
    mockSuccessfulCreateQueue();
    mockFailedAttachQueue();
    mockSuccessfulDeleteFunction();
    mockFailedDeleteQueue();
    assertThrows(FailedResourceDeletionException.class, runCommand());
  }

  @Test
  public void throwsExceptionWhenAttachQueueAndDeleteFunctionAndDeleteQueueFails() {
    mockRequest();
    mockSuccessfulGetRecord();
    mockSuccessfulCreateFunction();
    mockSuccessfulCreateQueue();
    mockFailedAttachQueue();
    mockFailedDeleteFunction();
    mockFailedDeleteQueue();
    assertThrows(FailedResourceDeletionException.class, runCommand());
  }

  @Test
  public void throwsExceptionWhenPutRecordFails() {
    mockRequest();
    mockSuccessfulGetRecord();
    mockSuccessfulCreateFunction();
    mockSuccessfulCreateQueue();
    mockSuccessfulAttachQueue();
    mockFailedPutRecord();
    mockSuccessfulDeleteFunction();
    mockSuccessfulDeleteQueue();
    assertThrows(FailedStorageWriteException.class, runCommand());
  }

  @Test
  public void throwsExceptionWhenPutRecordAndDeleteFunctionFails() {
    mockRequest();
    mockSuccessfulGetRecord();
    mockSuccessfulCreateFunction();
    mockSuccessfulCreateQueue();
    mockSuccessfulAttachQueue();
    mockFailedPutRecord();
    mockFailedDeleteFunction();
    mockSuccessfulDeleteQueue();
    assertThrows(FailedResourceDeletionException.class, runCommand());
  }

  @Test
  public void throwsExceptionWhenPutRecordAndDeleteQueueFails() {
    mockRequest();
    mockSuccessfulGetRecord();
    mockSuccessfulCreateFunction();
    mockSuccessfulCreateQueue();
    mockSuccessfulAttachQueue();
    mockFailedPutRecord();
    mockSuccessfulDeleteFunction();
    mockFailedDeleteQueue();
    assertThrows(FailedResourceDeletionException.class, runCommand());
  }

  @Test
  public void throwsExceptionWhenPutRecordAndDeleteFunctionAndDeleteQueueFails() {
    mockRequest();
    mockSuccessfulGetRecord();
    mockSuccessfulCreateFunction();
    mockSuccessfulCreateQueue();
    mockSuccessfulAttachQueue();
    mockFailedPutRecord();
    mockFailedDeleteFunction();
    mockFailedDeleteQueue();
    assertThrows(FailedResourceDeletionException.class, runCommand());
  }

  private void mockFailedDeleteFunction() {
    when(functionService.delete(FUNCTION_ID)).thenReturn(failedResourceDeletion());
  }

  private void mockGetRequestNodeId() {
    when(request.nodeId()).thenReturn(NODE_ID);
  }

  private void mockSuccessfulGetRecord() {
    when(storageService.get(NODE_ID)).thenReturn(noReturn());
  }

  private void mockExistingNodeRecord() {
    when(storageService.get(NODE_ID)).thenReturn(existingNodeRecord());
  }

  private void mockFailedGetRecord() {
    when(storageService.get(NODE_ID)).thenReturn(failedStorageRead());
  }

  private void mockFailedDeleteQueue() {
    when(queueService.delete(QUEUE_ID)).thenReturn(failedResourceDeletion());
  }

  // Immutables requires non-null attributes, so the request must be mocked.
  private void mockRequest() {
    when(request.functionConfig()).thenReturn(functionConfig);
    mockGetRequestNodeId();
    when(request.queueConfig()).thenReturn(queueConfig);
  }

  private void mockSuccessfulCreateFunction() {
    when(functionService.create(functionConfig)).thenReturn(function());
  }

  private void mockSuccessfulCreateQueue() {
    when(queueService.create(queueConfig)).thenReturn(queue());
  }

  private void mockSuccessfulAttachQueue() {
    when(functionService.attachQueue(FUNCTION_ID, QUEUE_ID, queueConfig))
        .thenReturn(eventSourceMappingId());
  }

  private void mockSuccessfulPutRecord() {
    when(storageService.put(nodeRecord())).thenReturn(noReturn());
  }

  private CreateNodeResponse response() {
    return CreateNodeResponse.builder()
        .nodeId(NODE_ID)
        .functionId(FUNCTION_ID)
        .queueId(QUEUE_ID)
        .build();
  }

  private CreateNodeResponse run() {
    return command.run(request);
  }

  private NodeRecord nodeRecord() {
    return NodeRecord.builder().nodeId(NODE_ID).functionId(FUNCTION_ID).queueId(QUEUE_ID).build();
  }

  private void mockFailedCreateFunction() {
    when(functionService.create(functionConfig)).thenReturn(failedResource());
  }

  private void mockSuccessfulDeleteQueue() {
    when(queueService.delete(QUEUE_ID)).thenReturn(noReturn());
  }

  private Executable runCommand() {
    return this::run;
  }

  private void mockFailedCreateQueue() {
    when(queueService.create(queueConfig)).thenReturn(failedResource());
  }

  private void mockFailedAttachQueue() {
    when(functionService.attachQueue(FUNCTION_ID, QUEUE_ID, queueConfig))
        .thenReturn(failedEventSourceMapping());
  }

  private <T> CompletionStage<T> failedEventSourceMapping() {
    return CompletableFuture.failedFuture(new FailedEventSourceMappingException());
  }

  private void mockFailedPutRecord() {
    when(storageService.put(nodeRecord())).thenReturn(failedStorageWrite());
  }

  private void mockSuccessfulDeleteFunction() {
    when(functionService.delete(FUNCTION_ID)).thenReturn(noReturn());
  }

  private <T> CompletionStage<T> failedStorageWrite() {
    return CompletableFuture.failedFuture(new FailedStorageWriteException());
  }

  private <T> CompletionStage<T> existingNodeRecord() {
    return CompletableFuture.failedFuture(new NodeAlreadyExistsException());
  }

  private <T> CompletionStage<T> failedStorageRead() {
    return CompletableFuture.failedFuture(new FailedStorageReadException());
  }

  private <T> CompletionStage<T> failedResourceDeletion() {
    return CompletableFuture.failedFuture(new FailedResourceDeletionException());
  }

  private CompletionStage<Resource> failedResource() {
    Resource failed = Resource.builder().exception(new FailedResourceCreationException()).build();
    return CompletableFuture.completedFuture(failed);
  }

  private <T> CompletionStage<T> noReturn() {
    return CompletableFuture.completedFuture(null);
  }

  private CompletionStage<Resource> function() {
    return CompletableFuture.completedFuture(Resource.builder().id(FUNCTION_ID).build());
  }

  private CompletionStage<Resource> queue() {
    return CompletableFuture.completedFuture(Resource.builder().id(QUEUE_ID).build());
  }

  private CompletionStage<String> eventSourceMappingId() {
    return CompletableFuture.completedFuture(EVENT_SOURCE_MAPPING_ID);
  }
}
