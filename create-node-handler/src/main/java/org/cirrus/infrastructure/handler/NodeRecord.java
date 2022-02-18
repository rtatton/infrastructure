package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cirrus.infrastructure.util.DynamoDbImmutableStyle;
import org.cirrus.infrastructure.util.Keys;
import org.immutables.value.Value;

@DynamoDbImmutableStyle
@Value.Immutable
public abstract class NodeRecord {

  public static Builder builder() {
    return ImmutableNodeRecord.builder();
  }

  @JsonProperty(Keys.NODE_KEY)
  public abstract String getNodeId();

  @JsonProperty(Keys.FUNCTION_KEY)
  public abstract String getFunctionId();

  @JsonProperty(Keys.QUEUE_KEY)
  public abstract String getQueueId();

  public interface Builder {

    NodeRecord build();

    Builder nodeId(String nodeId);

    Builder functionId(String functionId);

    Builder queueId(String queueId);
  }
}
