import org.cirrus.infrastructure.handler.util.Logger;

final class DeleteQueueLoggerFactory {

  private static final DeleteQueueComponent component = DaggerDeleteQueueComponent.create();

  public static Logger create() {
    return component.getLogger();
  }
}
