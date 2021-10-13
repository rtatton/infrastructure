import org.cirrus.infrastructure.handler.util.Logger;

final class CreateQueueLoggerFactory {

  private static final Logger logger = DaggerCreateQueueComponent.create().getLogger();

  public static Logger create() {
    return logger;
  }
}
