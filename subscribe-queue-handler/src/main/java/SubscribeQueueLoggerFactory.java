import org.cirrus.infrastructure.handler.util.Logger;

final class SubscribeQueueLoggerFactory {

  private static final Logger logger = DaggerSubscribeQueueComponent.create().getLogger();

  public static Logger create() {
    return logger;
  }
}
