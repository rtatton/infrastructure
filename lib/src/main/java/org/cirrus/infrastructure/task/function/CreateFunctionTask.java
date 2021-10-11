package org.cirrus.infrastructure.task.function;

import java.util.function.Consumer;
import org.cirrus.infrastructure.task.resource.CreateResourceTask;
import org.cirrus.infrastructure.task.resource.ResourceType;
import org.cirrus.infrastructure.task.util.Command;

public final class CreateFunctionTask extends CreateResourceTask {

  private static final ResourceType TYPE = ResourceType.FUNCTION;
  private static final Command<String> command = CreateFunction.create();
  private static final Consumer<Throwable> logger = DaggerFunctionComponent.create().newLogger();

  public CreateFunctionTask() {
    super(TYPE, command, logger);
  }
}
