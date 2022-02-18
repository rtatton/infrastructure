package org.cirrus.infrastructure.handler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cirrus.infrastructure.util.Keys;
import org.immutables.value.Value;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbImmutable(builder = NodeRecord.Builder.class)
@Value.Immutable
public abstract class NodeRecord {

  public static Builder builder() {
    return ImmutableNodeRecord.builder();
  }

  @DynamoDbPartitionKey
  @DynamoDbAttribute(Keys.NODE_KEY)
  @JsonProperty(Keys.NODE_KEY)
  public abstract String nodeId();

  @DynamoDbAttribute(Keys.FUNCTION_KEY)
  @JsonProperty(Keys.FUNCTION_KEY)
  public abstract String functionId();

  @DynamoDbAttribute(Keys.QUEUE_KEY)
  @JsonProperty(Keys.QUEUE_KEY)
  public abstract String queueId();

  public interface Builder {

    NodeRecord build();

    Builder nodeId(String nodeId);

    Builder functionId(String functionId);

    Builder queueId(String queueId);
  }
}
