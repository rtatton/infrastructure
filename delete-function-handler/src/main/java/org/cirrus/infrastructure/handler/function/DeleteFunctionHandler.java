package org.cirrus.infrastructure.handler.function;

import org.cirrus.infrastructure.handler.resource.CreateResourcesOutput;
import org.cirrus.infrastructure.handler.resource.delete.DeleteResourceHandler;
import org.cirrus.infrastructure.handler.util.Logger;
import org.cirrus.infrastructure.handler.util.Resource;

public final class DeleteFunctionHandler extends DeleteResourceHandler {

  private static final Logger logger = DeleteFunctionLoggerFactory.create();

  public DeleteFunctionHandler() {
    super(logger);
  }

  @Override
  public void deleteResource(CreateResourcesOutput input) {
    String resourceId = getResourceId(input);
    DeleteFunctionCommand.create(resourceId).run();
  }

  private String getResourceId(CreateResourcesOutput input) {
    return input.getTypedOutputs().get(Resource.FUNCTION).getResourceId();
  }
}
