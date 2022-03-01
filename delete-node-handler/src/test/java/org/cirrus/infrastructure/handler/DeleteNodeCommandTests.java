package org.cirrus.infrastructure.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import org.cirrus.infrastructure.handler.exception.FailedResourceDeletionException;
import org.cirrus.infrastructure.handler.exception.NoSuchNodeException;
import org.cirrus.infrastructure.handler.fixtures.HandlerTests;
import org.cirrus.infrastructure.handler.model.DeleteNodeRequest;
import org.cirrus.infrastructure.handler.model.NodeRecord;
import org.cirrus.infrastructure.handler.service.FunctionService;
import org.cirrus.infrastructure.handler.service.QueueService;
import org.cirrus.infrastructure.handler.service.StorageService;
import org.cirrus.infrastructure.handler.util.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class DeleteNodeCommandTests {

  private static final String NODE_ID = HandlerTests.NODE_ID;
  private static final String FUNCTION_ID = HandlerTests.FUNCTION_ID;
  private static final String QUEUE_ID = HandlerTests.QUEUE_ID;
  @Mock private FunctionService functionService;
  @Mock private QueueService queueService;
  @Mock private StorageService<NodeRecord> storageService;
  @Mock private Mapper mapper;
  @Mock private DeleteNodeRequest request;

  @InjectMocks private DeleteNodeCommand command;

  @BeforeEach
  public void setUp() {
    mockRequest();
  }

  @Test
  public void doesNotThrowWhenSuccessful() {
    mockSuccessfulDeleteRecord();
    mockSuccessfulDeleteFunction();
    mockSuccessfulDeleteQueue();
    assertDoesNotThrow(this::run);
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

  // Immutables requires non-null attributes, so the request must be mocked.
  private void mockRequest() {
    when(request.nodeId()).thenReturn(NODE_ID);
  }

  private void mockFailedDeleteQueue() {
    when(queueService.delete(QUEUE_ID)).thenReturn(HandlerTests.failedResourceDeletion());
  }

  private void mockFailedDeleteFunction() {
    when(functionService.delete(FUNCTION_ID)).thenReturn(HandlerTests.failedResourceDeletion());
  }

  private void mockSuccessfulDeleteFunction() {
    when(functionService.delete(FUNCTION_ID)).thenReturn(HandlerTests.noReturn());
  }

  private void mockSuccessfulDeleteQueue() {
    when(queueService.delete(QUEUE_ID)).thenReturn(HandlerTests.noReturn());
  }

  private void mockSuccessfulDeleteRecord() {
    when(storageService.delete(NODE_ID)).thenReturn(HandlerTests.nodeRecordStage());
  }

  private void mockNonexistentNodeRecord() {
    when(storageService.delete(NODE_ID)).thenReturn(HandlerTests.noSuchNode());
  }

  private void run() {
    command.run(request);
  }
}
