package org.cirrus.infrastructure.task.queue;

import java.util.function.Consumer;
import org.cirrus.infrastructure.task.resource.DeleteResourceTask;
import org.cirrus.infrastructure.task.resource.Resource;

public class DeleteQueueTask extends DeleteResourceTask {

  private static final Consumer<Throwable> logger = DaggerQueueComponent.create().getLogger();

  public DeleteQueueTask() {
    super(Resource.QUEUE, logger);
  }

  @Override
  public void deleteResource(String queueId) {
    DeleteQueue.create(queueId).run();
  }
}
