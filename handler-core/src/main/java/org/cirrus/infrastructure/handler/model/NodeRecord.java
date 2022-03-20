package org.cirrus.infrastructure.handler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cirrus.infrastructure.util.Keys;
import org.cirrus.infrastructure.util.Preconditions;
import org.immutables.value.Value;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Value.Immutable
@JsonSerialize(as = ImmutableNodeRecord.class)
@JsonDeserialize(as = ImmutableNodeRecord.class)
@DynamoDbImmutable(builder = NodeRecord.Builder.class)
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

  @DynamoDbAttribute(Keys.ARTIFACT_ID)
  @JsonProperty(Keys.ARTIFACT_ID)
  public abstract String artifactId();

  @Value.Check
  protected void check() {
    Preconditions.checkNotNullOrEmpty(nodeId());
    Preconditions.checkNotNullOrEmpty(functionId());
    Preconditions.checkNotNullOrEmpty(queueId());
    Preconditions.checkNotNullOrEmpty(artifactId());
  }

  public abstract static class Builder {

    public abstract NodeRecord build();

    public abstract Builder nodeId(String nodeId);

    public abstract Builder functionId(String functionId);

    public abstract Builder queueId(String queueId);

    public abstract Builder artifactId(String artifactId);
  }
}
