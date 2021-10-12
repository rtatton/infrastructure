package org.cirrus.infrastructure.task.resource;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.function.Consumer;
import org.cirrus.infrastructure.task.util.MappingUtil;

public abstract class DeleteResourceTask implements RequestHandler<String, String> {

  private final ResourceType type;
  private final Consumer<Throwable> logger;

  public DeleteResourceTask(ResourceType type, Consumer<Throwable> logger) {
    this.type = type;
    this.logger = logger;
  }

  @Override
  public String handleRequest(String input, Context context) {
    DeleteResourceInput mappedInput = mapInput(input);
    String resourceId = getResourceId(mappedInput);
    deleteResource(resourceId);
    DeleteResourceOutput output = createOutput(mappedInput);
    return mapOutput(output);
  }

  public DeleteResourceInput mapInput(String input) {
    return MappingUtil.read(input, DeleteResourceInput.class, logger);
  }

  public String getResourceId(DeleteResourceInput input) {
    return input.getTypedOutputs().get(type).getResourceId();
  }

  public abstract void deleteResource(String resourceId);

  public DeleteResourceOutput createOutput(DeleteResourceInput input) {
    return DeleteResourceOutput.newBuilder().addAllOutputs(input.getOutputs()).build();
  }

  public String mapOutput(DeleteResourceOutput output) {
    return MappingUtil.write(output, logger);
  }
}
