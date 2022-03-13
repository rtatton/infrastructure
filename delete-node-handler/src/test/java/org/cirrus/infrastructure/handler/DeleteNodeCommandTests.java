package org.cirrus.infrastructure.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.cirrus.infrastructure.handler.exception.FailedResourceDeletionException;
import org.cirrus.infrastructure.handler.exception.NoSuchNodeException;
import org.cirrus.infrastructure.handler.model.NodeRecord;
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
class DeleteNodeCommandTests {

  @Mock private FunctionService functionService;
  @Mock private QueueService queueService;
  @Mock private StorageService<NodeRecord> storageService;
  @Mock private DeleteNodeRequest request;
  @InjectMocks private DeleteNodeCommand command;

  @BeforeEach
  public void setUp() {
    mockRequest();
  }

  @Test
  public void returnResponseWhenSuccessful() {
    mockSuccessfulDeleteRecord();
    mockSuccessfulDeleteFunction();
    mockSuccessfulDeleteQueue();
    assertEquals(deleteNodeResponse(), run());
  }

  @Test
  public void throwsExceptionWhenNodeDoesNotExist() {
    mockNonexistentNodeRecord();
    assertThrows(NoSuchNodeException.class, this::run);
  }

  @Test
  public void throwsExceptionWhenDeleteFunctionFails() {
    mockSuccessfulDeleteRecord();
    mockFailedDeleteFunction();
    mockSuccessfulDeleteQueue();
    assertThrows(FailedResourceDeletionException.class, this::run);
  }

  @Test
  public void throwsExceptionWhenDeleteQueueFails() {
    mockSuccessfulDeleteRecord();
    mockSuccessfulDeleteFunction();
    mockFailedDeleteQueue();
    assertThrows(FailedResourceDeletionException.class, this::run);
  }

  @Test
  public void throwsExceptionWhenDeleteFunctionAndDeleteQueueFails() {
    mockSuccessfulDeleteRecord();
    mockFailedDeleteFunction();
    mockFailedDeleteQueue();
    assertThrows(FailedResourceDeletionException.class, this::run);
  }

  private DeleteNodeResponse deleteNodeResponse() {
    return DeleteNodeResponse.create();
  }

  // Immutables requires non-null attributes, so the request must be mocked.
  private void mockRequest() {
    when(request.nodeId()).thenReturn(HandlerTests.NODE_ID);
  }

  private void mockFailedDeleteQueue() {
    when(queueService.deleteQueue(HandlerTests.QUEUE_ID))
        .thenReturn(HandlerTests.failedResourceDeletion());
  }

  private void mockFailedDeleteFunction() {
    when(functionService.deleteFunction(HandlerTests.FUNCTION_ID))
        .thenReturn(HandlerTests.failedResourceDeletion());
  }

  private void mockSuccessfulDeleteFunction() {
    when(functionService.deleteFunction(HandlerTests.FUNCTION_ID))
        .thenReturn(HandlerTests.noReturn());
  }

  private void mockSuccessfulDeleteQueue() {
    when(queueService.deleteQueue(HandlerTests.QUEUE_ID)).thenReturn(HandlerTests.noReturn());
  }

  private void mockSuccessfulDeleteRecord() {
    when(storageService.deleteItem(HandlerTests.NODE_ID))
        .thenReturn(HandlerTests.nodeRecordStage());
  }

  private void mockNonexistentNodeRecord() {
    when(storageService.deleteItem(HandlerTests.NODE_ID)).thenReturn(HandlerTests.noSuchNode());
  }

  private DeleteNodeResponse run() {
    return command.run(request);
  }
}
