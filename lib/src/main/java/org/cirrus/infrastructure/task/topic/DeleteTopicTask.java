package org.cirrus.infrastructure.task.topic;

import java.util.function.Consumer;
import org.cirrus.infrastructure.task.resource.DeleteResourceTask;
import org.cirrus.infrastructure.task.resource.Resource;

public final class DeleteTopicTask extends DeleteResourceTask {

  private static final Consumer<Throwable> logger = DaggerTopicComponent.create().newLogger();

  public DeleteTopicTask() {
    super(Resource.TOPIC, logger);
  }

  @Override
  public void deleteResource(String topicId) {
    DeleteTopic.create(topicId).run();
  }
}
