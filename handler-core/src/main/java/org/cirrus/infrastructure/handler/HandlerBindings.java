package org.cirrus.infrastructure.handler;

import dagger.Binds;
import dagger.Module;
import javax.inject.Singleton;
import org.cirrus.infrastructure.handler.model.NodeRecord;
import org.cirrus.infrastructure.handler.service.DynamoDbStorageService;
import org.cirrus.infrastructure.handler.service.FunctionService;
import org.cirrus.infrastructure.handler.service.LambdaFunctionService;
import org.cirrus.infrastructure.handler.service.QueueService;
import org.cirrus.infrastructure.handler.service.SqsQueueService;
import org.cirrus.infrastructure.handler.service.StorageService;
import org.cirrus.infrastructure.logging.ConsoleLogger;
import org.cirrus.infrastructure.logging.Logger;
import org.cirrus.infrastructure.util.JacksonMapper;
import org.cirrus.infrastructure.util.Mapper;

@Module
interface HandlerBindings {

  @Binds
  @Singleton
  Mapper bindMapper(JacksonMapper mapper);

  @Binds
  @Singleton
  Logger bindLogger(ConsoleLogger logger);

  @Binds
  @Singleton
  FunctionService bindFunctionService(LambdaFunctionService service);

  @Binds
  @Singleton
  QueueService bindQueueService(SqsQueueService service);

  @Binds
  @Singleton
  StorageService<NodeRecord> bindStorageService(DynamoDbStorageService service);
}
