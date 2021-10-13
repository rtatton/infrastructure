import org.cirrus.infrastructure.handler.util.Logger;

final class AddQueueLoggerFactory {

  private static final Logger logger = DaggerAddQueueComponent.create().getLogger();

  public static Logger create() {
    return logger;
  }
}
