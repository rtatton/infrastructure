package org.cirrus.infrastructure.task.queue;

import java.util.function.Consumer;
import org.cirrus.infrastructure.task.resource.DeleteResourceHandler;
import org.cirrus.infrastructure.task.resource.Resource;

public class DeleteQueueHandler extends DeleteResourceHandler {

  private static final Consumer<Throwable> logger = DaggerQueueComponent.create().getLogger();

  public DeleteQueueHandler() {
    super(Resource.QUEUE, logger);
  }

  @Override
  public void deleteResource(String queueId) {
    DeleteQueueCommand.create(queueId).run();
  }
}
