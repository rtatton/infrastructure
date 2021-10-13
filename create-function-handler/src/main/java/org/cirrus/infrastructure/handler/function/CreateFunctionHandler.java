package org.cirrus.infrastructure.handler.function;

import org.cirrus.infrastructure.handler.resource.CreateResourceHandler;
import org.cirrus.infrastructure.handler.resource.CreateResourceInput;
import org.cirrus.infrastructure.handler.util.Logger;
import org.cirrus.infrastructure.handler.util.Resource;

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
