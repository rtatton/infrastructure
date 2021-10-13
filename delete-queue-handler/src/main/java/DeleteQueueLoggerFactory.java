import org.cirrus.infrastructure.handler.util.Logger;

final class DeleteQueueLoggerFactory {

  private static final Logger logger = DaggerDeleteQueueComponent.create().getLogger();

  public static Logger create() {
    return logger;
  }
}
