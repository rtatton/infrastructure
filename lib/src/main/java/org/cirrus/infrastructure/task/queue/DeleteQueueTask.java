package org.cirrus.infrastructure.task.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Consumer;
import org.cirrus.infrastructure.task.resource.DeleteResourceTask;
import org.cirrus.infrastructure.task.resource.ResourceType;
import org.cirrus.infrastructure.task.util.ObjectMapperFactory;

public class DeleteQueueTask extends DeleteResourceTask {

  private static final ResourceType TYPE = ResourceType.QUEUE;
  private static final Consumer<Throwable> logger = DaggerQueueComponent.create().newLogger();
  private static final ObjectMapper mapper = ObjectMapperFactory.create();

  public DeleteQueueTask() {
    super(TYPE, mapper, logger);
  }

  @Override
  public void deleteResource(String queueId) {
    DeleteQueue.create(queueId).run();
  }
}
