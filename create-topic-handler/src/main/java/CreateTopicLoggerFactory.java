import org.cirrus.infrastructure.handler.util.Logger;

final class CreateTopicLoggerFactory {

  private static final CreateTopicComponent component = DaggerCreateTopicComponent.create();

  public static Logger create() {
    return component.getLogger();
  }
}
