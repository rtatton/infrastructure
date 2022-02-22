package org.cirrus.infrastructure.handler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.cirrus.infrastructure.handler.exception.FailedEventSourceMappingException;
import org.cirrus.infrastructure.handler.exception.FailedResourceCreationException;
import org.cirrus.infrastructure.handler.exception.FailedResourceDeletionException;
import org.cirrus.infrastructure.handler.exception.FailedStorageReadException;
import org.cirrus.infrastructure.handler.exception.FailedStorageWriteException;
import org.cirrus.infrastructure.handler.exception.NodeAlreadyExistsException;
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
   * Creates a cloud-based node with computing and messaging capabilities.
   *
   * @param request Contains the identifier of the node and resource configuration.
   * @throws NodeAlreadyExistsException Thrown when the requested node identifier already exists.
   * @throws FailedStorageReadException Thrown when an error occurs when attempting to access the
   *     storage service to check if the requested node identifier already exists.
   * @throws FailedResourceCreationException Thrown when any of the resources fail to be created.
   * @throws FailedResourceDeletionException Thrown when any of the created cloud resources fail to
   *     be deleted. During creation of the node, this is thrown when attempting to rollback after
   *     either (1) only some node resources could be successfully created or (2) any of the
   *     proceeding steps, after creating the node resources, fails.
   * @throws FailedEventSourceMappingException Thrown after successfully creating resources, but
   *     failing to add the node queue to the node function as an event source mapping. If, in the
   *     processing of rolling back, one of the created resources fails to be deleted, this
   *     exception is overridden by a {@link FailedResourceDeletionException}.
   * @throws FailedStorageWriteException Thrown after successfully creating resources and adding the
   *     queue as an event source mapping to the function, but failing to store the resource
   *     identifiers. If, in the processing of rolling back, one of the created resources fails to
   *     be deleted, this exception is overridden by a {@link FailedResourceDeletionException}.
   * @return A response containing the resource identifiers of the node.
   */
  // TODO Update Javadoc
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
