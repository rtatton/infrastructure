package org.cirrus.infrastructure.task.topic;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Consumer;
import org.cirrus.infrastructure.task.resource.CreateResourceTask;
import org.cirrus.infrastructure.task.resource.ResourceType;
import org.cirrus.infrastructure.task.util.Command;
import org.cirrus.infrastructure.task.util.ObjectMapperFactory;

public final class CreateTopicTask extends CreateResourceTask {

  private static final ResourceType TYPE = ResourceType.TOPIC;
  private static final Command<String> command = CreateTopic.create();
  private static final Consumer<Throwable> logger = DaggerTopicComponent.create().newLogger();
  private static final ObjectMapper mapper = ObjectMapperFactory.create();

  public CreateTopicTask() {
    super(TYPE, command, mapper, logger);
  }
}
