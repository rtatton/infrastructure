package org.cirrus.infrastructure.handler.queue;

import org.cirrus.infrastructure.handler.resource.CreateResourcesOutput;
import org.cirrus.infrastructure.handler.resource.delete.DeleteResourceHandler;
import org.cirrus.infrastructure.util.Logger;
import org.cirrus.infrastructure.util.Resource;

public final class DeleteQueueHandler extends DeleteResourceHandler {

  private static final Logger logger = DeleteQueueLoggerFactory.create();

  public DeleteQueueHandler() {
    super(logger);
  }

  @Override
  public void deleteResource(CreateResourcesOutput input) {
    String resourceId = getResourceId(input);
    DeleteQueueCommand.create(resourceId).run();
  }

  private String getResourceId(CreateResourcesOutput input) {
    return input.getTypedOutputs().get(Resource.QUEUE).getResourceId();
  }
}
