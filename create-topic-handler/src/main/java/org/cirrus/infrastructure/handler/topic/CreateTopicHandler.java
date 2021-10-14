package org.cirrus.infrastructure.handler.topic;

import org.cirrus.infrastructure.handler.resource.CreateResourceHandler;
import org.cirrus.infrastructure.handler.resource.CreateResourceInput;
import org.cirrus.infrastructure.util.Logger;
import org.cirrus.infrastructure.util.Resource;

public final class CreateTopicHandler extends CreateResourceHandler {

  private static final Logger logger = CreateTopicLoggerFactory.create();

  public CreateTopicHandler() {
    super(Resource.TOPIC, logger);
  }

  @Override
  public String createResource(CreateResourceInput input) {
    return CreateTopicCommand.create().run();
  }
}
