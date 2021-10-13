package org.cirrus.infrastructure.handler.resource.delete;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.cirrus.infrastructure.handler.resource.CreateResourcesOutput;
import org.cirrus.infrastructure.handler.util.Logger;
import org.cirrus.infrastructure.handler.util.MappingUtil;

public abstract class DeleteResourceHandler implements RequestHandler<String, String> {

  private final Logger logger;

  public DeleteResourceHandler(Logger logger) {
    this.logger = logger;
  }

  @Override
  public String handleRequest(String input, Context context) {
    CreateResourcesOutput mappedInput = mapInput(input);
    deleteResource(mappedInput);
    return input;
  }

  public CreateResourcesOutput mapInput(String input) {
    return MappingUtil.read(input, CreateResourcesOutput.class, logger);
  }

  public abstract void deleteResource(CreateResourcesOutput input);
}