package org.cirrus.infrastructure.handler.service;

import java.util.concurrent.CompletionStage;

public interface StorageService<T> {

  CompletionStage<Void> put(T value);

  CompletionStage<T> get(Object key);
}
