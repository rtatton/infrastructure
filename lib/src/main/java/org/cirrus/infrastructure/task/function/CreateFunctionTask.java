package org.cirrus.infrastructure.task.function;

import java.util.function.Consumer;
import org.cirrus.infrastructure.task.Command;
import org.cirrus.infrastructure.task.resource.CreateResourceTask;
import org.cirrus.infrastructure.task.resource.Resource;

public final class CreateFunctionTask extends CreateResourceTask {

  private static final Command<String> command = CreateFunction.create();
  private static final Consumer<Throwable> logger = DaggerFunctionComponent.create().getLogger();

  public CreateFunctionTask() {
    super(Resource.FUNCTION, command, logger);
  }
}
