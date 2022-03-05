package org.cirrus.infrastructure.handler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cirrus.infrastructure.util.Keys;
import org.immutables.value.Value;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbImmutable(builder = NodeRecord.Builder.class)
@Value.Immutable
public interface NodeRecord {

  static Builder builder() {
    return ImmutableNodeRecord.builder();
  }

  @DynamoDbPartitionKey
  @DynamoDbAttribute(Keys.NODE_ID)
  @JsonProperty(Keys.NODE_ID)
  String nodeId();

  @DynamoDbAttribute(Keys.FUNCTION_ID)
  @JsonProperty(Keys.FUNCTION_ID)
  String functionId();

  @DynamoDbAttribute(Keys.QUEUE_ID)
  @JsonProperty(Keys.QUEUE_ID)
  String queueId();

  interface Builder {

    NodeRecord build();

    Builder nodeId(String nodeId);

    Builder functionId(String functionId);

    Builder queueId(String queueId);
  }
}
