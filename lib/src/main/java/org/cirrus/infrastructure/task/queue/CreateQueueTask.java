package org.cirrus.infrastructure.task.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Consumer;
import org.cirrus.infrastructure.task.resource.CreateResourceTask;
import org.cirrus.infrastructure.task.resource.ResourceType;
import org.cirrus.infrastructure.task.util.Command;
import org.cirrus.infrastructure.task.util.ObjectMapperFactory;

public final class CreateQueueTask extends CreateResourceTask {

  private static final ResourceType TYPE = ResourceType.QUEUE;
  private static final Command<String> command = CreateQueue.create();
  private static final Consumer<Throwable> logger = DaggerQueueComponent.create().newLogger();
  private static final ObjectMapper mapper = ObjectMapperFactory.create();

  public CreateQueueTask() {
    super(TYPE, command, mapper, logger);
  }
}
