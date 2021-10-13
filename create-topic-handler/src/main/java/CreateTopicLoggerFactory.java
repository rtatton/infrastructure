import org.cirrus.infrastructure.handler.util.Logger;

final class CreateTopicLoggerFactory {

  private static final Logger logger = DaggerCreateTopicComponent.create().getLogger();

  public static Logger create() {
    return logger;
  }
}
