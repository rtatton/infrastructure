package org.cirrus.infrastructure.task.resource;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Consumer;
import org.cirrus.infrastructure.task.exception.ObjectMappingException;

public abstract class DeleteResourceTask implements RequestHandler<String, String> {

  private final ResourceType type;
  private final ObjectMapper mapper;
  private final Consumer<Throwable> logger;

  public DeleteResourceTask(ResourceType type, ObjectMapper mapper, Consumer<Throwable> logger) {
    this.type = type;
    this.mapper = mapper;
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
    try {
      return mapper.readValue(input, DeleteResourceInput.class);
    } catch (JsonProcessingException exception) {
      logger.accept(exception);
      throw new ObjectMappingException();
    }
  }

  public String getResourceId(DeleteResourceInput input) {
    return input.getTypedOutputs().get(type).getResourceId();
  }

  public abstract void deleteResource(String resourceId);

  public DeleteResourceOutput createOutput(DeleteResourceInput input) {
    return DeleteResourceOutput.newBuilder().addAllOutputs(input.getOutputs()).build();
  }

  public String mapOutput(DeleteResourceOutput output) {
    try {
      return mapper.writeValueAsString(output);
    } catch (JsonProcessingException exception) {
      logger.accept(exception);
      throw new ObjectMappingException();
    }
  }
}
