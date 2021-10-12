package org.cirrus.infrastructure.task.topic;

import java.util.function.Consumer;
import org.cirrus.infrastructure.task.resource.DeleteResourceHandler;
import org.cirrus.infrastructure.task.resource.Resource;

public final class DeleteTopicHandler extends DeleteResourceHandler {

  private static final Consumer<Throwable> logger = DaggerTopicComponent.create().getLogger();

  public DeleteTopicHandler() {
    super(Resource.TOPIC, logger);
  }

  @Override
  public void deleteResource(String topicId) {
    DeleteTopicCommand.create(topicId).run();
  }
}
