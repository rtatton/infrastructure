package org.cirrus.infrastructure.task.topic;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Consumer;
import org.cirrus.infrastructure.task.resource.DeleteResourceTask;
import org.cirrus.infrastructure.task.resource.ResourceType;
import org.cirrus.infrastructure.task.util.ObjectMapperFactory;

public final class DeleteTopicTask extends DeleteResourceTask {

  private static final ResourceType TYPE = ResourceType.TOPIC;
  private static final Consumer<Throwable> logger = DaggerTopicComponent.create().newLogger();
  private static final ObjectMapper mapper = ObjectMapperFactory.create();

  public DeleteTopicTask() {
    super(TYPE, mapper, logger);
  }

  @Override
  public void deleteResource(String topicId) {
    DeleteTopic.create(topicId).run();
  }
}
