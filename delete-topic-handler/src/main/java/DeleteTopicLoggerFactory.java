import org.cirrus.infrastructure.handler.util.Logger;

final class DeleteTopicLoggerFactory {

  private static final DeleteTopicComponent component = DaggerDeleteTopicComponent.create();

  public static Logger create() {
    return component.getLogger();
  }
}
