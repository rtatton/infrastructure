package org.cirrus.infrastructure.task.function;

import java.util.function.Consumer;
import org.cirrus.infrastructure.task.resource.DeleteResourceTask;
import org.cirrus.infrastructure.task.resource.ResourceType;

public final class DeleteFunctionTask extends DeleteResourceTask {

  private static final ResourceType TYPE = ResourceType.FUNCTION;
  private static final Consumer<Throwable> logger = DaggerFunctionComponent.create().newLogger();

  public DeleteFunctionTask() {
    super(TYPE, logger);
  }

  @Override
  public void deleteResource(String functionId) {
    DeleteFunction.create(functionId).run();
  }
}
