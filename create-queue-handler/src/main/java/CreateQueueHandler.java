import org.cirrus.infrastructure.handler.resource.CreateResourceHandler;
import org.cirrus.infrastructure.handler.resource.CreateResourceInput;
import org.cirrus.infrastructure.handler.util.Logger;
import org.cirrus.infrastructure.handler.util.Resource;

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
