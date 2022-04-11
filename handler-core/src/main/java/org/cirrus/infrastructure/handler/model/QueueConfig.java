package org.cirrus.infrastructure.handler.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cirrus.infrastructure.util.Preconditions;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableQueueConfig.class)
@JsonDeserialize(as = ImmutableQueueConfig.class)
public abstract class QueueConfig {

  public static Builder builder() {
    return ImmutableQueueConfig.builder();
  }

  @Value.Default
  public int messageRetentionPeriodSeconds() {
    return 345600;
  }

  @Value.Default
  public int delaySeconds() {
    return 0;
  }

  @Value.Default
  public int maxMessageSizeBytes() {
    return 262144;
  }

  @Value.Default
  public int receiveMessageWaitTimeSeconds() {
    return 0;
  }

  @Value.Default
  public int visibilityTimeoutSeconds() {
    return 30;
  }

  @Value.Default
  public int batchSize() {
    return 10;
  }

  @Value.Check
  protected void check() {
    Preconditions.checkInRangeClosed(delaySeconds(), 0, 900);
    Preconditions.checkInRangeClosed(maxMessageSizeBytes(), 1024, 262144);
    Preconditions.checkInRangeClosed(receiveMessageWaitTimeSeconds(), 0, 20);
    Preconditions.checkInRangeClosed(visibilityTimeoutSeconds(), 0, 43200);
    Preconditions.checkInRangeClosed(batchSize(), 0, 10);
  }

  public abstract static class Builder {

    public abstract QueueConfig build();

    public abstract Builder delaySeconds(int delay);

    public abstract Builder maxMessageSizeBytes(int maximumSize);

    public abstract Builder messageRetentionPeriodSeconds(int retentionPeriod);

    public abstract Builder receiveMessageWaitTimeSeconds(int receiveWaitTime);

    public abstract Builder visibilityTimeoutSeconds(int visibilityTimeout);

    public abstract Builder batchSize(int batchSize);
  }
}
