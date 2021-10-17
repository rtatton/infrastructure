package org.cirrus.infrastructure.handler;

import org.cirrus.infrastructure.util.Logger;
import org.cirrus.infrastructure.util.Resource;

public final class CreateQueueHandler extends CreateResourceHandler {

  private static final Logger logger = CreateQueueLoggerFactory.create();

  public CreateQueueHandler() {
    super(Resource.QUEUE, logger);
  }

  @Override
  public String createResource(CreateResourceInput input) {
    return CreateQueueCommand.create().run();
  }
}
