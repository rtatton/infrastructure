package org.cirrus.infrastructure.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.cirrus.infrastructure.util.Logger;
import org.cirrus.infrastructure.util.Mapper;
import org.cirrus.infrastructure.util.Resource;

public final class SubscribeQueueHandler implements RequestHandler<String, String> {

  private static final Mapper mapper = Mapper.create();
  private static final Logger logger = SubscribeQueueLoggerFactory.create();

  @Override
  public String handleRequest(String input, Context context) {
    CreateResourcesOutput mappedInput = mapInput(input);
    subscribeQueue(mappedInput);
    return input;
  }

  private CreateResourcesOutput mapInput(String input) {
    return mapper.read(input, CreateResourcesOutput.class, logger);
  }

  private void subscribeQueue(CreateResourcesOutput input) {
    String topicId = getTopicId(input);
    String queueId = getQueueId(input);
    SubscribeQueueCommand.create(topicId, queueId).run();
  }

  private String getTopicId(CreateResourcesOutput input) {
    return getResourceId(input, Resource.TOPIC);
  }

  private String getQueueId(CreateResourcesOutput input) {
    return getResourceId(input, Resource.QUEUE);
  }

  private String getResourceId(CreateResourcesOutput input, Resource type) {
    return input.getTypedOutputs().get(type).getResourceId();
  }
}
