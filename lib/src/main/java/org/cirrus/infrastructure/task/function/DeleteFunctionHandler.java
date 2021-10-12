package org.cirrus.infrastructure.task.function;

import java.util.function.Consumer;
import org.cirrus.infrastructure.task.resource.DeleteResourceHandler;
import org.cirrus.infrastructure.task.resource.Resource;

public final class DeleteFunctionHandler extends DeleteResourceHandler {

  private static final Consumer<Throwable> logger = DaggerFunctionComponent.create().getLogger();

  public DeleteFunctionHandler() {
    super(Resource.FUNCTION, logger);
  }

  @Override
  public void deleteResource(String functionId) {
    DeleteFunctionCommand.create(functionId).run();
  }
}
