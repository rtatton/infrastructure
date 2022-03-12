package org.cirrus.infrastructure.handler.service;

import java.util.concurrent.CompletableFuture;

public interface StorageService<T> {

  CompletableFuture<Void> putItem(T value);

  CompletableFuture<T> getItem(String key);

  CompletableFuture<T> deleteItem(String key);
}
