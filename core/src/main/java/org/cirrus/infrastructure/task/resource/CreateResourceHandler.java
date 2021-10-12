package org.cirrus.infrastructure.task.resource;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.function.Consumer;
import org.cirrus.infrastructure.task.Command;
import org.cirrus.infrastructure.task.util.MappingUtil;

public class CreateResourceHandler implements RequestHandler<String, String> {

  private final Resource type;
  private final Command<String> command;
  private final Consumer<Throwable> logger;

  public CreateResourceHandler(Resource type, Command<String> command, Consumer<Throwable> logger) {
    this.type = type;
    this.command = command;
    this.logger = logger;
  }

  @Override
  public String handleRequest(String input, Context context) {
    String resourceId = command.run();
    CreateResourceInput mappedInput = mapInput(input);
    CreateResourceOutput output = createOutput(mappedInput, resourceId);
    return mapOutput(output);
  }

  public CreateResourceInput mapInput(String input) {
    return MappingUtil.read(input, CreateResourceInput.class, logger);
  }

  public CreateResourceOutput createOutput(CreateResourceInput input, String resourceId) {
    return CreateResourceOutput.newBuilder()
        .setName(input.getName())
        .setResourceId(resourceId)
        .setType(type)
        .build();
  }

  public String mapOutput(CreateResourceOutput output) {
    return MappingUtil.write(output, logger);
  }
}
