package org.cirrus.infrastructure.handler.topic;

import org.cirrus.infrastructure.handler.resource.CreateResourcesOutput;
import org.cirrus.infrastructure.handler.resource.delete.DeleteResourceHandler;
import org.cirrus.infrastructure.handler.util.Logger;
import org.cirrus.infrastructure.handler.util.Resource;

public final class DeleteTopicHandler extends DeleteResourceHandler {

  private static final Logger logger = DeleteTopicLoggerFactory.create();

  public DeleteTopicHandler() {
    super(logger);
  }

  @Override
  public void deleteResource(CreateResourcesOutput input) {
    String resourceId = getResourceId(input);
    DeleteTopicCommand.create(resourceId).run();
  }

  private String getResourceId(CreateResourcesOutput input) {
    return input.getTypedOutputs().get(Resource.TOPIC).getResourceId();
  }
}
