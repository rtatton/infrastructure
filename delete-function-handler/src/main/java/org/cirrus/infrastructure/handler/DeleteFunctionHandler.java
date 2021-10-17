package org.cirrus.infrastructure.handler;

import org.cirrus.infrastructure.util.Logger;
import org.cirrus.infrastructure.util.Resource;

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
