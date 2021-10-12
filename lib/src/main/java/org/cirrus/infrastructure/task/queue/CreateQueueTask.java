package org.cirrus.infrastructure.task.queue;

import java.util.function.Consumer;
import org.cirrus.infrastructure.task.Command;
import org.cirrus.infrastructure.task.resource.CreateResourceTask;
import org.cirrus.infrastructure.task.resource.Resource;

public final class CreateQueueTask extends CreateResourceTask {

  private static final Command<String> command = CreateQueueCommand.create();
  private static final Consumer<Throwable> logger = DaggerQueueComponent.create().getLogger();

  public CreateQueueTask() {
    super(Resource.QUEUE, command, logger);
  }
}
