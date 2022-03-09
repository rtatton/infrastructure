package org.cirrus.infrastructure.handler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.inject.Inject;
import org.cirrus.infrastructure.handler.exception.FailedResourceDeletionException;
import org.cirrus.infrastructure.handler.exception.FailedStorageDeleteException;
import org.cirrus.infrastructure.handler.exception.NoSuchNodeException;
import org.cirrus.infrastructure.handler.model.NodeRecord;
import org.cirrus.infrastructure.handler.service.FunctionService;
import org.cirrus.infrastructure.handler.service.QueueService;
import org.cirrus.infrastructure.handler.service.StorageService;
import org.cirrus.infrastructure.handler.util.Mapper;

final class DeleteNodeCommand implements Command<DeleteNodeRequest, DeleteNodeResponse> {

  private final FunctionService functionService;
  private final QueueService queueService;
  private final StorageService<NodeRecord> storageService;
  private final Mapper mapper;

  @Inject
  public DeleteNodeCommand(
      FunctionService functionService,
      QueueService queueService,
      StorageService<NodeRecord> storageService,
      Mapper mapper) {
    this.functionService = functionService;
    this.queueService = queueService;
    this.storageService = storageService;
    this.mapper = mapper;
  }

  /**
   * Deletes a cloud-based node with computing and messaging capabilities.
   *
   * @param request Contains the identifier of the node to delete.
   * @throws NoSuchNodeException Thrown when the requested node identifier does not exist.
   * @throws FailedStorageDeleteException Thrown when an error occurs when attempting to access the
   *     storage service to delete the requested node resource identifiers.
   * @throws FailedResourceDeletionException Thrown when any of cloud resources fail to be deleted.
   * @return An empty response.
   */
  public DeleteNodeResponse run(DeleteNodeRequest request) {
    try {
      CompletableFuture<NodeRecord> getRecord = deleteRecord(request.nodeId());
      CompletableFuture.allOf(deleteFunction(getRecord), deleteQueue(getRecord)).join();
      return DeleteNodeResponse.create();
    } catch (CompletionException exception) {
      throw (RuntimeException) exception.getCause();
    }
  }

  /**
   * @param request JSON-formatted {@link DeleteNodeRequest}
   * @return JSON-formatted {@link DeleteNodeResponse}
   * @see DeleteNodeCommand#run(DeleteNodeRequest)
   */
  public String runFromString(String request) {
    mapToOutPut(run(mapToInput(request)));
    return null;
  }

  private CompletableFuture<?> deleteFunction(CompletableFuture<NodeRecord> getRecord) {
    return getRecord.thenComposeAsync(record -> deleteFunction(record.functionId()));
  }

  private CompletableFuture<?> deleteQueue(CompletableFuture<NodeRecord> getRecord) {
    return getRecord.thenComposeAsync(record -> deleteQueue(record.queueId()));
  }

  private CompletableFuture<NodeRecord> deleteRecord(String nodeId) {
    return storageService.deleteItem(nodeId);
  }

  private DeleteNodeRequest mapToInput(String data) {
    return mapper.read(data, DeleteNodeRequest.class);
  }

  private String mapToOutPut(DeleteNodeResponse response) {
    return mapper.write(response);
  }

  private CompletableFuture<?> deleteQueue(String queueId) {
    return queueService.deleteQueue(queueId);
  }

  private CompletableFuture<?> deleteFunction(String functionId) {
    return functionService.deleteFunction(functionId);
  }
}
