package org.cirrus.infrastructure.handler;

import org.cirrus.infrastructure.util.Logger;
import org.cirrus.infrastructure.util.Resource;

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
