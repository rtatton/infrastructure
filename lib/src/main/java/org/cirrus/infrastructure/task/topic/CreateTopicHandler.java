package org.cirrus.infrastructure.task.topic;

import java.util.function.Consumer;
import org.cirrus.infrastructure.task.Command;
import org.cirrus.infrastructure.task.resource.CreateResourceHandler;
import org.cirrus.infrastructure.task.resource.Resource;

public final class CreateTopicHandler extends CreateResourceHandler {

  private static final Command<String> command = CreateTopicCommand.create();
  private static final Consumer<Throwable> logger = DaggerTopicComponent.create().getLogger();

  public CreateTopicHandler() {
    super(Resource.TOPIC, command, logger);
  }
}
