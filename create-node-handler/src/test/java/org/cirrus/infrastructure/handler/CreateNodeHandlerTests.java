package org.cirrus.infrastructure.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import org.cirrus.infrastructure.handler.exception.FailedEventSourceMappingException;
import org.cirrus.infrastructure.handler.exception.FailedResourceCreationException;
import org.cirrus.infrastructure.handler.exception.FailedResourceDeletionException;
import org.cirrus.infrastructure.handler.exception.FailedStorageReadException;
import org.cirrus.infrastructure.handler.exception.FailedStorageWriteException;
import org.cirrus.infrastructure.handler.exception.NodeAlreadyExistsException;
import org.cirrus.infrastructure.handler.fixtures.HandlerTests;
import org.cirrus.infrastructure.handler.model.CreateNodeRequest;
import org.cirrus.infrastructure.handler.model.CreateNodeResponse;
import org.cirrus.infrastructure.handler.model.FunctionConfig;
import org.cirrus.infrastructure.handler.model.NodeRecord;
import org.cirrus.infrastructure.handler.model.QueueConfig;
import org.cirrus.infrastructure.handler.service.FunctionService;
import org.cirrus.infrastructure.handler.service.QueueService;
import org.cirrus.infrastructure.handler.service.StorageService;
import org.cirrus.infrastructure.util.Mapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class CreateNodeHandlerTests {

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
    assertEquals(HandlerTests.createNodeResponse(), run());
  }

  @Test
  public void throwsExceptionWhenGetRecordFails() {
    mockGetRequestNodeId();
    mockFailedGetRecord();
    assertThrows(FailedStorageReadException.class, this::run);
  }

  @Test
  public void throwsExceptionWhenNodeAlreadyExists() {
    mockGetRequestNodeId();
    mockExistingNodeRecord();
    assertThrows(NodeAlreadyExistsException.class, this::run);
  }

  @Test
  public void throwsExceptionWhenCreateFunctionFails() {
    mockRequest();
    mockSuccessfulGetRecord();
    mockFailedCreateFunction();
    mockSuccessfulCreateQueue();
    mockSuccessfulDeleteQueue();
    assertThrows(FailedResourceCreationException.class, this::run);
  }

  @Test
  public void throwsExceptionWhenCreateFunctionAndDeleteQueueFails() {
    mockRequest();
    mockSuccessfulGetRecord();
    mockFailedCreateFunction();
    mockSuccessfulCreateQueue();
    mockFailedDeleteQueue();
    assertThrows(FailedResourceDeletionException.class, this::run);
  }

  @Test
  public void throwsExceptionWhenCreateQueueFails() {
    mockRequest();
    mockSuccessfulGetRecord();
    mockSuccessfulCreateFunction();
    mockFailedCreateQueue();
    mockSuccessfulDeleteFunction();
    assertThrows(FailedResourceCreationException.class, this::run);
  }

  @Test
  public void throwsExceptionWhenCreateQueueAndDeleteFunctionFails() {
    mockRequest();
    mockSuccessfulGetRecord();
    mockSuccessfulCreateFunction();
    mockFailedCreateQueue();
    mockFailedDeleteFunction();
    assertThrows(FailedResourceDeletionException.class, this::run);
  }

  @Test
  public void throwsExceptionWhenCreateFunctionAndCreateQueueFails() {
    mockRequest();
    mockSuccessfulGetRecord();
    mockFailedCreateFunction();
    mockFailedCreateQueue();
    assertThrows(FailedResourceCreationException.class, this::run);
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
    assertThrows(FailedEventSourceMappingException.class, this::run);
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
    assertThrows(FailedResourceDeletionException.class, this::run);
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
    assertThrows(FailedResourceDeletionException.class, this::run);
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
    assertThrows(FailedResourceDeletionException.class, this::run);
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
    assertThrows(FailedStorageWriteException.class, this::run);
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
    assertThrows(FailedResourceDeletionException.class, this::run);
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
    assertThrows(FailedResourceDeletionException.class, this::run);
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
    assertThrows(FailedResourceDeletionException.class, this::run);
  }

  private void mockFailedDeleteFunction() {
    when(functionService.delete(HandlerTests.FUNCTION_ID))
        .thenReturn(HandlerTests.failedResourceDeletion());
  }

  private void mockGetRequestNodeId() {
    when(request.nodeId()).thenReturn(HandlerTests.NODE_ID);
  }

  // Record should not exist yet before creating the node
  private void mockSuccessfulGetRecord() {
    when(storageService.get(HandlerTests.NODE_ID)).thenReturn(HandlerTests.noReturn());
  }

  private void mockExistingNodeRecord() {
    when(storageService.get(HandlerTests.NODE_ID)).thenReturn(HandlerTests.nodeAlreadyExists());
  }

  private void mockFailedGetRecord() {
    when(storageService.get(HandlerTests.NODE_ID)).thenReturn(HandlerTests.failedStorageRead());
  }

  private void mockFailedDeleteQueue() {
    when(queueService.delete(HandlerTests.QUEUE_ID))
        .thenReturn(HandlerTests.failedResourceDeletion());
  }

  // Immutables requires non-null attributes, so the request must be mocked.
  private void mockRequest() {
    when(request.functionConfig()).thenReturn(functionConfig);
    mockGetRequestNodeId();
    when(request.queueConfig()).thenReturn(queueConfig);
  }

  private void mockSuccessfulCreateFunction() {
    when(functionService.create(functionConfig)).thenReturn(HandlerTests.function());
  }

  private void mockSuccessfulCreateQueue() {
    when(queueService.create(queueConfig)).thenReturn(HandlerTests.queue());
  }

  private void mockSuccessfulAttachQueue() {
    when(functionService.attachQueue(HandlerTests.FUNCTION_ID, HandlerTests.QUEUE_ID, queueConfig))
        .thenReturn(HandlerTests.eventSourceMappingId());
  }

  private void mockSuccessfulPutRecord() {
    when(storageService.put(HandlerTests.nodeRecord())).thenReturn(HandlerTests.noReturn());
  }

  private CreateNodeResponse run() {
    return command.run(request);
  }

  private void mockFailedCreateFunction() {
    when(functionService.create(functionConfig)).thenReturn(HandlerTests.failedResource());
  }

  private void mockSuccessfulDeleteQueue() {
    when(queueService.delete(HandlerTests.QUEUE_ID)).thenReturn(HandlerTests.noReturn());
  }

  private void mockFailedCreateQueue() {
    when(queueService.create(queueConfig)).thenReturn(HandlerTests.failedResource());
  }

  private void mockFailedAttachQueue() {
    when(functionService.attachQueue(HandlerTests.FUNCTION_ID, HandlerTests.QUEUE_ID, queueConfig))
        .thenReturn(HandlerTests.failedEventSourceMapping());
  }

  private void mockFailedPutRecord() {
    when(storageService.put(HandlerTests.nodeRecord()))
        .thenReturn(HandlerTests.failedStorageWrite());
  }

  private void mockSuccessfulDeleteFunction() {
    when(functionService.delete(HandlerTests.FUNCTION_ID)).thenReturn(HandlerTests.noReturn());
  }
}
