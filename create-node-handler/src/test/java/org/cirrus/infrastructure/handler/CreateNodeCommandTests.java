package org.cirrus.infrastructure.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.cirrus.infrastructure.handler.exception.FailedEventSourceMappingException;
import org.cirrus.infrastructure.handler.exception.FailedResourceCreationException;
import org.cirrus.infrastructure.handler.exception.FailedResourceDeletionException;
import org.cirrus.infrastructure.handler.exception.FailedStorageWriteException;
import org.cirrus.infrastructure.handler.model.FunctionConfig;
import org.cirrus.infrastructure.handler.model.NodeRecord;
import org.cirrus.infrastructure.handler.model.QueueConfig;
import org.cirrus.infrastructure.handler.service.FunctionService;
import org.cirrus.infrastructure.handler.service.QueueService;
import org.cirrus.infrastructure.handler.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateNodeCommandTests {

  @Mock private FunctionService functionService;
  @Mock private QueueService queueService;
  @Mock private StorageService<NodeRecord> storageService;
  @Mock private FunctionConfig functionConfig;
  @Mock private QueueConfig queueConfig;
  @Mock private CreateNodeRequest request;
  @InjectMocks private CreateNodeCommand command;

  @BeforeEach
  public void setUp() {
    mockRequest();
  }

  @Test
  public void returnsResponseWhenSuccessful() {
    mockSuccessfulCreateFunction();
    mockSuccessfulCreateQueue();
    mockSuccessfulAttachQueue();
    mockSuccessfulPutRecord();
    CreateNodeResponse response = run();
    assertEquals(HandlerTests.FUNCTION_ID, response.functionId());
    assertEquals(HandlerTests.QUEUE_ID, response.queueId());
    assertEquals(HandlerTests.ARTIFACT_ID, response.artifactId());
  }

  @Test
  public void throwsExceptionWhenCreateFunctionFails() {
    mockFailedCreateFunction();
    mockSuccessfulCreateQueue();
    mockSuccessfulDeleteQueue();
    assertThrows(FailedResourceCreationException.class, this::run);
  }

  @Test
  public void throwsExceptionWhenCreateFunctionAndDeleteQueueFails() {
    mockFailedCreateFunction();
    mockSuccessfulCreateQueue();
    mockFailedDeleteQueue();
    assertThrows(FailedResourceDeletionException.class, this::run);
  }

  @Test
  public void throwsExceptionWhenCreateQueueFails() {
    mockSuccessfulCreateFunction();
    mockFailedCreateQueue();
    mockSuccessfulDeleteFunction();
    assertThrows(FailedResourceCreationException.class, this::run);
  }

  @Test
  public void throwsExceptionWhenCreateQueueAndDeleteFunctionFails() {
    mockSuccessfulCreateFunction();
    mockFailedCreateQueue();
    mockFailedDeleteFunction();
    assertThrows(FailedResourceDeletionException.class, this::run);
  }

  @Test
  public void throwsExceptionWhenCreateFunctionAndCreateQueueFails() {
    mockFailedCreateFunction();
    mockFailedCreateQueue();
    assertThrows(FailedResourceCreationException.class, this::run);
  }

  @Test
  public void throwsExceptionWhenAttachQueueFails() {
    mockSuccessfulCreateFunction();
    mockSuccessfulCreateQueue();
    mockFailedAttachQueue();
    mockSuccessfulDeleteFunction();
    mockSuccessfulDeleteQueue();
    assertThrows(FailedEventSourceMappingException.class, this::run);
  }

  @Test
  public void throwsExceptionWhenAttachQueueAndDeleteFunctionFails() {
    mockSuccessfulCreateFunction();
    mockSuccessfulCreateQueue();
    mockFailedAttachQueue();
    mockFailedDeleteFunction();
    mockSuccessfulDeleteQueue();
    assertThrows(FailedResourceDeletionException.class, this::run);
  }

  @Test
  public void throwsExceptionWhenAttachQueueAndDeleteQueueFails() {
    mockSuccessfulCreateFunction();
    mockSuccessfulCreateQueue();
    mockFailedAttachQueue();
    mockSuccessfulDeleteFunction();
    mockFailedDeleteQueue();
    assertThrows(FailedResourceDeletionException.class, this::run);
  }

  @Test
  public void throwsExceptionWhenAttachQueueAndDeleteFunctionAndDeleteQueueFails() {
    mockSuccessfulCreateFunction();
    mockSuccessfulCreateQueue();
    mockFailedAttachQueue();
    mockFailedDeleteFunction();
    mockFailedDeleteQueue();
    assertThrows(FailedResourceDeletionException.class, this::run);
  }

  @Test
  public void throwsExceptionWhenPutRecordFails() {
    mockSuccessfulCreateFunction();
    mockSuccessfulCreateQueue();
    mockSuccessfulAttachQueue();
    mockFailedPutRecord();
    mockSuccessfulDeleteFunction();
    mockSuccessfulDeleteQueue();
    assertThrows(FailedStorageWriteException.class, this::run);
  }

  @Test
  public void throwsExceptionWhenPutRecordAndDeleteFunctionFails() {
    mockSuccessfulCreateFunction();
    mockSuccessfulCreateQueue();
    mockSuccessfulAttachQueue();
    mockFailedPutRecord();
    mockFailedDeleteFunction();
    mockSuccessfulDeleteQueue();
    assertThrows(FailedResourceDeletionException.class, this::run);
  }

  @Test
  public void throwsExceptionWhenPutRecordAndDeleteQueueFails() {
    mockSuccessfulCreateFunction();
    mockSuccessfulCreateQueue();
    mockSuccessfulAttachQueue();
    mockFailedPutRecord();
    mockSuccessfulDeleteFunction();
    mockFailedDeleteQueue();
    assertThrows(FailedResourceDeletionException.class, this::run);
  }

  @Test
  public void throwsExceptionWhenPutRecordAndDeleteFunctionAndDeleteQueueFails() {
    mockSuccessfulCreateFunction();
    mockSuccessfulCreateQueue();
    mockSuccessfulAttachQueue();
    mockFailedPutRecord();
    mockFailedDeleteFunction();
    mockFailedDeleteQueue();
    assertThrows(FailedResourceDeletionException.class, this::run);
  }

  private void mockFailedDeleteFunction() {
    when(functionService.deleteFunction(HandlerTests.FUNCTION_ID))
        .thenReturn(HandlerTests.failedResourceDeletion());
  }

  private void mockGetArtifactId() {
    when(functionConfig.artifactId()).thenReturn(HandlerTests.ARTIFACT_ID);
  }

  private void mockFailedDeleteQueue() {
    when(queueService.deleteQueue(HandlerTests.QUEUE_ID))
        .thenReturn(HandlerTests.failedResourceDeletion());
  }

  // Immutables requires non-null attributes, so the request must be mocked.
  private void mockRequest() {
    when(request.functionConfig()).thenReturn(functionConfig);
    mockGetArtifactId();
    when(request.queueConfig()).thenReturn(queueConfig);
  }

  private void mockSuccessfulCreateFunction() {
    when(functionService.createFunction(functionConfig)).thenReturn(HandlerTests.function());
  }

  private void mockSuccessfulCreateQueue() {
    when(queueService.createQueue(queueConfig)).thenReturn(HandlerTests.queue());
  }

  private void mockSuccessfulAttachQueue() {
    when(functionService.attachQueue(HandlerTests.FUNCTION_ID, HandlerTests.QUEUE_ID, queueConfig))
        .thenReturn(HandlerTests.eventSourceMappingId());
  }

  private void mockSuccessfulPutRecord() {
    // Random node ID is generated, so we have to match based on the class type.
    when(storageService.putItem(any(NodeRecord.class))).thenReturn(HandlerTests.noReturn());
  }

  private CreateNodeResponse run() {
    return command.run(request);
  }

  private void mockFailedCreateFunction() {
    when(functionService.createFunction(functionConfig)).thenReturn(HandlerTests.failedResource());
  }

  private void mockSuccessfulDeleteQueue() {
    when(queueService.deleteQueue(HandlerTests.QUEUE_ID)).thenReturn(HandlerTests.noReturn());
  }

  private void mockFailedCreateQueue() {
    when(queueService.createQueue(queueConfig)).thenReturn(HandlerTests.failedResource());
  }

  private void mockFailedAttachQueue() {
    when(functionService.attachQueue(HandlerTests.FUNCTION_ID, HandlerTests.QUEUE_ID, queueConfig))
        .thenReturn(HandlerTests.failedEventSourceMapping());
  }

  private void mockFailedPutRecord() {
    when(storageService.putItem(any(NodeRecord.class)))
        .thenReturn(HandlerTests.failedStorageWrite());
  }

  private void mockSuccessfulDeleteFunction() {
    when(functionService.deleteFunction(HandlerTests.FUNCTION_ID))
        .thenReturn(HandlerTests.noReturn());
  }
}
