package org.cirrus.infrastructure.task.function;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.function.Consumer;
import org.cirrus.infrastructure.task.resource.ResourceType;
import org.cirrus.infrastructure.task.util.Mapping;

public class AddQueueTask implements RequestHandler<String, String> {

  private static final ResourceType FUNCTION = ResourceType.FUNCTION;
  private static final ResourceType QUEUE = ResourceType.QUEUE;
  private static final Consumer<Throwable> logger = DaggerFunctionComponent.create().newLogger();

  @Override
  public String handleRequest(String input, Context context) {
    AddQueueInput mappedInput = mapInput(input);
    String functionId = getFunctionId(mappedInput);
    String queueId = getQueueId(mappedInput);
    addQueue(functionId, queueId);
    AddQueueOutput output = createOutput(mappedInput);
    return mapOutput(output);
  }

  private AddQueueInput mapInput(String input) {
    return Mapping.read(input, AddQueueInput.class, logger);
  }

  private String getFunctionId(AddQueueInput input) {
    return getResourceId(input, FUNCTION);
  }

  private String getQueueId(AddQueueInput input) {
    return getResourceId(input, QUEUE);
  }

  private void addQueue(String functionId, String queueId) {
    AddQueue.create(functionId, queueId).run();
  }

  private AddQueueOutput createOutput(AddQueueInput input) {
    return AddQueueOutput.newBuilder().addAllOutputs(input.getOutputs()).build();
  }

  private String mapOutput(AddQueueOutput output) {
    return Mapping.write(output, logger);
  }

  private String getResourceId(AddQueueInput input, ResourceType type) {
    return input.getTypedOutputs().get(type).getResourceId();
  }
}
