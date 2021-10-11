package org.cirrus.infrastructure.task.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Consumer;
import org.cirrus.infrastructure.task.resource.CreateResourceTask;
import org.cirrus.infrastructure.task.resource.ResourceType;
import org.cirrus.infrastructure.task.util.Command;
import org.cirrus.infrastructure.task.util.ObjectMapperFactory;

public final class CreateFunctionTask extends CreateResourceTask {

  private static final ResourceType TYPE = ResourceType.FUNCTION;
  private static final Command<String> command = CreateFunction.create();
  private static final Consumer<Throwable> logger = DaggerFunctionComponent.create().newLogger();
  private static final ObjectMapper mapper = ObjectMapperFactory.create();

  public CreateFunctionTask() {
    super(TYPE, command, mapper, logger);
  }
}
