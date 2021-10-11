package org.cirrus.infrastructure.task.queue;

import java.util.function.Consumer;
import org.cirrus.infrastructure.task.resource.CreateResourceTask;
import org.cirrus.infrastructure.task.resource.ResourceType;
import org.cirrus.infrastructure.task.util.Command;

public final class CreateQueueTask extends CreateResourceTask {

  private static final ResourceType TYPE = ResourceType.QUEUE;
  private static final Command<String> command = CreateQueue.create();
  private static final Consumer<Throwable> logger = DaggerQueueComponent.create().newLogger();

  public CreateQueueTask() {
    super(TYPE, command, logger);
  }
}
