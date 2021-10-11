package org.cirrus.infrastructure.task.resource;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Consumer;
import org.cirrus.infrastructure.task.exception.ObjectMappingException;
import org.cirrus.infrastructure.task.util.Command;

public class CreateResourceTask implements RequestHandler<String, String> {

  private final ResourceType type;
  private final Command<String> command;
  private final ObjectMapper mapper;
  private final Consumer<Throwable> logger;

  public CreateResourceTask(
      ResourceType type, Command<String> command, ObjectMapper mapper, Consumer<Throwable> logger) {
    this.type = type;
    this.command = command;
    this.mapper = mapper;
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
    try {
      return mapper.readValue(input, CreateResourceInput.class);
    } catch (JsonProcessingException exception) {
      logger.accept(exception);
      throw new ObjectMappingException();
    }
  }

  public CreateResourceOutput createOutput(CreateResourceInput input, String resourceId) {
    return CreateResourceOutput.newBuilder()
        .setName(input.getName())
        .setResourceId(resourceId)
        .setType(type)
        .build();
  }

  public String mapOutput(CreateResourceOutput output) {
    try {
      return mapper.writeValueAsString(output);
    } catch (JsonProcessingException exception) {
      logger.accept(exception);
      throw new ObjectMappingException();
    }
  }
}
