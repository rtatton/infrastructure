package org.cirrus.infrastructure.task.queue;

import java.util.function.Consumer;
import org.cirrus.infrastructure.task.resource.DeleteResourceTask;
import org.cirrus.infrastructure.task.resource.ResourceType;

public class DeleteQueueTask extends DeleteResourceTask {

  private static final ResourceType TYPE = ResourceType.QUEUE;
  private static final Consumer<Throwable> logger = DaggerQueueComponent.create().newLogger();

  public DeleteQueueTask() {
    super(TYPE, logger);
  }

  @Override
  public void deleteResource(String queueId) {
    DeleteQueue.create(queueId).run();
  }
}
