package org.cirrus.infrastructure.task.function;

import java.util.function.Consumer;
import org.cirrus.infrastructure.task.Command;
import org.cirrus.infrastructure.task.resource.CreateResourceHandler;
import org.cirrus.infrastructure.task.resource.Resource;

public final class CreateFunctionHandler extends CreateResourceHandler {

  private static final Command<String> command = CreateFunctionCommand.create();
  private static final Consumer<Throwable> logger = DaggerFunctionComponent.create().getLogger();

  public CreateFunctionHandler() {
    super(Resource.FUNCTION, command, logger);
  }
}
