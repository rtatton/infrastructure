package org.cirrus.infrastructure.handler;

import org.cirrus.infrastructure.util.Logger;
import org.cirrus.infrastructure.util.Resource;

public final class CreateFunctionHandler extends CreateResourceHandler {

  private static final Logger logger = CreateFunctionLoggerFactory.create();

  public CreateFunctionHandler() {
    super(Resource.FUNCTION, logger);
  }

  @Override
  public String createResource(CreateResourceInput input) {
    return CreateFunctionCommand.create().run();
  }
}
