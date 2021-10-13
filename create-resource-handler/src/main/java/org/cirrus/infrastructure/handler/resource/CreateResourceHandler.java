package org.cirrus.infrastructure.handler.resource;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.cirrus.infrastructure.handler.util.Logger;
import org.cirrus.infrastructure.handler.util.Mapper;
import org.cirrus.infrastructure.handler.util.Resource;

public abstract class CreateResourceHandler implements RequestHandler<String, String> {

  private static final Mapper mapper = Mapper.create();
  private final Resource type;
  private final Logger logger;

  public CreateResourceHandler(Resource type, Logger logger) {
    this.type = type;
    this.logger = logger;
  }

  @Override
  public String handleRequest(String input, Context context) {
    CreateResourceInput mappedInput = mapInput(input);
    String resourceId = createResource(mappedInput);
    CreateResourceOutput output = createOutput(mappedInput, resourceId);
    return mapOutput(output);
  }

  public CreateResourceInput mapInput(String input) {
    return mapper.read(input, CreateResourceInput.class, logger);
  }

  public abstract String createResource(CreateResourceInput input);

  public CreateResourceOutput createOutput(CreateResourceInput input, String resourceId) {
    return CreateResourceOutput.newBuilder()
        .setName(input.getName())
        .setResourceId(resourceId)
        .setType(type)
        .build();
  }

  public String mapOutput(CreateResourceOutput output) {
    return mapper.write(output, logger);
  }
}
