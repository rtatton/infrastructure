import org.cirrus.infrastructure.handler.util.Logger;

final class CreateQueueLoggerFactory {

  private static final CreateQueueComponent component = DaggerCreateQueueComponent.create();

  public static Logger create() {
    return component.getLogger();
  }
}
