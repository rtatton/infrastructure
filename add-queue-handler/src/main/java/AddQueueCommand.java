import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import org.cirrus.infrastructure.handler.util.Command;
import org.cirrus.infrastructure.handler.util.Logger;
import org.cirrus.infrastructure.handler.util.ResourceUtil;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.CreateEventSourceMappingRequest;
import software.amazon.awssdk.services.lambda.model.CreateEventSourceMappingResponse;

final class AddQueueCommand implements Command<Void> {

  private static final AddQueueComponent component = DaggerAddQueueComponent.create();
  private final LambdaAsyncClient receiver;
  private final BiFunction<String, String, CreateEventSourceMappingRequest> requester;
  private final Logger logger;
  private final String functionId;
  private final String queueId;

  @AssistedInject
  AddQueueCommand(
      LambdaAsyncClient receiver,
      BiFunction<String, String, CreateEventSourceMappingRequest> requester,
      Logger logger,
      @Assisted("functionId") String functionId,
      @Assisted("queueId") String queueId) {
    this.receiver = receiver;
    this.requester = requester;
    this.logger = logger;
    this.functionId = functionId;
    this.queueId = queueId;
  }

  public static Command<Void> create(String functionId, String queueId) {
    return component.getAddQueueCommandFactory().create(functionId, queueId);
  }

  @Override
  public Void run() {
    CreateEventSourceMappingRequest request = requester.apply(functionId, queueId);
    Future<CreateEventSourceMappingResponse> response = receiver.createEventSourceMapping(request);
    ResourceUtil.logIfError(response, logger);
    return null;
  }
}
