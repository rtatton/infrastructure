package org.cirrus.infrastructure.handler.function;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.cirrus.infrastructure.handler.resource.CreateResourcesOutput;
import org.cirrus.infrastructure.handler.util.Logger;
import org.cirrus.infrastructure.handler.util.Mapper;
import org.cirrus.infrastructure.handler.util.Resource;

public class AddQueueHandler implements RequestHandler<String, String> {

  private static final Mapper mapper = Mapper.create();
  private static final Logger logger = AddQueueLoggerFactory.create();

  @Override
  public String handleRequest(String input, Context context) {
    CreateResourcesOutput mappedInput = mapInput(input);
    addQueue(mappedInput);
    return input;
  }

  private CreateResourcesOutput mapInput(String input) {
    return mapper.read(input, CreateResourcesOutput.class, logger);
  }

  private void addQueue(CreateResourcesOutput input) {
    String functionId = getFunctionId(input);
    String queueId = getQueueId(input);
    AddQueueCommand.create(functionId, queueId).run();
  }

  private String getFunctionId(CreateResourcesOutput input) {
    return getResourceId(input, Resource.FUNCTION);
  }

  private String getQueueId(CreateResourcesOutput input) {
    return getResourceId(input, Resource.QUEUE);
  }

  private String getResourceId(CreateResourcesOutput input, Resource type) {
    return input.getTypedOutputs().get(type).getResourceId();
  }
}
