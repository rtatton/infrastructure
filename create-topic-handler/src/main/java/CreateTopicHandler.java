import org.cirrus.infrastructure.handler.resource.CreateResourceHandler;
import org.cirrus.infrastructure.handler.resource.CreateResourceInput;
import org.cirrus.infrastructure.handler.util.Logger;
import org.cirrus.infrastructure.handler.util.Resource;

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
