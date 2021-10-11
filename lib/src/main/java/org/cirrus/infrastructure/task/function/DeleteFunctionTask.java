package org.cirrus.infrastructure.task.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Consumer;
import org.cirrus.infrastructure.task.resource.DeleteResourceTask;
import org.cirrus.infrastructure.task.resource.ResourceType;
import org.cirrus.infrastructure.task.util.ObjectMapperFactory;

public final class DeleteFunctionTask extends DeleteResourceTask {

  private static final ResourceType TYPE = ResourceType.FUNCTION;
  private static final Consumer<Throwable> logger = DaggerFunctionComponent.create().newLogger();
  private static final ObjectMapper mapper = ObjectMapperFactory.create();

  public DeleteFunctionTask() {
    super(TYPE, mapper, logger);
  }

  @Override
  public void deleteResource(String functionId) {
    DeleteFunction.create(functionId).run();
  }
}
