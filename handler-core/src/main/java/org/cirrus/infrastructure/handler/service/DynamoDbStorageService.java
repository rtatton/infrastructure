package org.cirrus.infrastructure.handler.service;

import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.cirrus.infrastructure.handler.exception.FailedStorageDeleteException;
import org.cirrus.infrastructure.handler.exception.FailedStorageReadException;
import org.cirrus.infrastructure.handler.exception.FailedStorageWriteException;
import org.cirrus.infrastructure.handler.exception.NoSuchNodeException;
import org.cirrus.infrastructure.handler.model.NodeRecord;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

public class DynamoDbStorageService implements StorageService<NodeRecord> {

  private final DynamoDbAsyncTable<NodeRecord> table;
  private final ServiceHelper helper;

  @Inject
  public DynamoDbStorageService(DynamoDbAsyncTable<NodeRecord> table, ServiceHelper helper) {
    this.table = table;
    this.helper = helper;
  }

  private static Key mapToKey(Object key) {
    return Key.builder().partitionValue((String) key).build();
  }

  private static NodeRecord throwIfAbsent(NodeRecord value) {
    if (value == null) {
      throw new NoSuchNodeException();
    }
    return value;
  }

  @Override
  public CompletableFuture<Void> putItem(NodeRecord value) {
    return helper.wrapThrowable(table.putItem(value), FailedStorageWriteException::new);
  }

  @Override
  public CompletableFuture<NodeRecord> getItem(Object key) {
    return helper
        .wrapThrowable(table.getItem(mapToKey(key)), FailedStorageReadException::new)
        .thenApplyAsync(DynamoDbStorageService::throwIfAbsent);
  }

  @Override
  public CompletableFuture<NodeRecord> deleteItem(Object key) {
    return helper
        .wrapThrowable(table.deleteItem(mapToKey(key)), FailedStorageDeleteException::new)
        .thenApplyAsync(DynamoDbStorageService::throwIfAbsent);
  }
}
