package org.cirrus.infrastructure.handler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cirrus.infrastructure.util.Keys;
import org.cirrus.infrastructure.util.Preconditions;
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
  @DynamoDbAttribute(Keys.NODE_ID)
  @JsonProperty(Keys.NODE_ID)
  public abstract String nodeId();

  @DynamoDbAttribute(Keys.FUNCTION_ID)
  @JsonProperty(Keys.FUNCTION_ID)
  public abstract String functionId();

  @DynamoDbAttribute(Keys.QUEUE_ID)
  @JsonProperty(Keys.QUEUE_ID)
  public abstract String queueId();

  @DynamoDbAttribute(Keys.CODE_ID)
  @JsonProperty(Keys.CODE_ID)
  public abstract String codeId();

  @Value.Check
  protected void check() {
    Preconditions.notNullOrEmpty(nodeId());
    Preconditions.notNullOrEmpty(functionId());
    Preconditions.notNullOrEmpty(queueId());
    Preconditions.notNullOrEmpty(codeId());
  }

  public interface Builder {

    NodeRecord build();

    Builder nodeId(String nodeId);

    Builder functionId(String functionId);

    Builder queueId(String queueId);

    Builder codeId(String codeId);
  }
}
