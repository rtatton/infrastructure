package org.cirrus.infrastructure.handler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.cirrus.infrastructure.handler.exception.FailedResourceDeletionException;
import org.cirrus.infrastructure.handler.exception.FailedStorageDeleteException;
import org.cirrus.infrastructure.handler.exception.FailedStorageReadException;
import org.cirrus.infrastructure.handler.exception.NoSuchNodeException;
import org.cirrus.infrastructure.handler.model.DeleteNodeRequest;
import org.cirrus.infrastructure.handler.model.NodeRecord;
import org.cirrus.infrastructure.handler.service.FunctionService;
import org.cirrus.infrastructure.handler.service.QueueService;
import org.cirrus.infrastructure.handler.service.StorageService;
import org.cirrus.infrastructure.util.Mapper;

@Singleton
public class DeleteNodeCommand implements Command<DeleteNodeRequest, Void> {

  private static final DeleteNodeComponent COMPONENT = DaggerDeleteNodeComponent.create();
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

  public static DeleteNodeCommand getInstance() {
    return COMPONENT.getCommand();
  }

  /**
   * Deletes a cloud-based node with computing and messaging capabilities.
   *
   * @param request Contains the identifier of the node to delete.
   * @throws NoSuchNodeException Thrown when the requested node identifier does not exist.
   * @throws FailedStorageReadException Thrown when an error occurs when attempting to access the
   *     storage service to retrieve requested node resource identifiers.
   * @throws FailedStorageDeleteException Thrown when an error occurs when attempting to access the
   *     storage service to delete the requested node resource identifiers.
   * @throws FailedResourceDeletionException Thrown when any of cloud resources fail to be deleted.
   * @return Null
   */
  public Void run(DeleteNodeRequest request) {
    try {
      CompletableFuture<NodeRecord> getRecord = deleteRecord(request.nodeId());
      CompletableFuture.allOf(deleteFunction(getRecord), deleteQueue(getRecord)).join();
      return null;
    } catch (CompletionException exception) {
      throw (RuntimeException) exception.getCause();
    }
  }

  /**
   * @param request JSON-formatted {@link DeleteNodeRequest}
   * @return Null string
   * @see DeleteNodeCommand#run(DeleteNodeRequest)
   */
  @Nullable
  public String runFromString(String request) {
    run(mapToInput(request));
    return null;
  }

  private CompletableFuture<?> deleteFunction(CompletableFuture<NodeRecord> getRecord) {
    return getRecord.thenComposeAsync(record -> deleteFunction(record.functionId()));
  }

  private CompletableFuture<?> deleteQueue(CompletableFuture<NodeRecord> getRecord) {
    return getRecord.thenComposeAsync(record -> deleteQueue(record.queueId()));
  }

  private CompletableFuture<NodeRecord> deleteRecord(String nodeId) {
    return storageService.delete(nodeId).toCompletableFuture();
  }

  private DeleteNodeRequest mapToInput(String data) {
    return mapper.read(data, DeleteNodeRequest.class);
  }

  private CompletableFuture<?> deleteQueue(String queueId) {
    return queueService.delete(queueId).toCompletableFuture();
  }

  private CompletableFuture<?> deleteFunction(String functionId) {
    return functionService.delete(functionId).toCompletableFuture();
  }
}
