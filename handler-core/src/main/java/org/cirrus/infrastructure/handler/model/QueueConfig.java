package org.cirrus.infrastructure.handler.model;

import org.cirrus.infrastructure.handler.util.Preconditions;
import org.immutables.value.Value;

@Value.Immutable
public abstract class QueueConfig {

  @Value.Default
  public int messageRetentionPeriodSeconds() {
    return 345_600;
  }

  @Value.Default
  public int delaySeconds() {
    return 0;
  }

  @Value.Default
  public int maxMessageSizeBytes() {
    return 262_144;
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
    Preconditions.inRangeClosed(delaySeconds(), 0, 900);
    Preconditions.inRangeClosed(maxMessageSizeBytes(), 1024, 262144);
    Preconditions.inRangeClosed(receiveMessageWaitTimeSeconds(), 0, 20);
    Preconditions.inRangeClosed(visibilityTimeoutSeconds(), 0, 43200);
    Preconditions.inRangeClosed(batchSize(), 0, 10);
  }

  public interface Builder {

    QueueConfig build();

    Builder delaySeconds(int delay);

    Builder maxMessageSizeBytes(int maximumSize);

    Builder messageRetentionPeriodSeconds(int retentionPeriod);

    Builder receiveMessageWaitTimeSeconds(int receiveWaitTime);

    Builder visibilityTimeoutSeconds(int visibilityTimeout);

    Builder batchSize(int batchSize);
  }
}
