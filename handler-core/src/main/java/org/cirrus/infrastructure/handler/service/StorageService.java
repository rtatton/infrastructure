package org.cirrus.infrastructure.handler.service;

import java.util.concurrent.CompletableFuture;

public interface StorageService<T> {

  CompletableFuture<Void> put(T value);

  CompletableFuture<T> get(Object key);

  CompletableFuture<T> delete(Object key);
}
