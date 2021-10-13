import org.cirrus.infrastructure.handler.util.Logger;

final class DeleteTopicLoggerFactory {

  private static final Logger logger = DaggerDeleteTopicComponent.create().getLogger();

  public static Logger create() {
    return logger;
  }
}
