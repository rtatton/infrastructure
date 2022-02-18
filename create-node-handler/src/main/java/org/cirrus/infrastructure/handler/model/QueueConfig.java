package org.cirrus.infrastructure.handler.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import org.immutables.value.Value;

@Value.Immutable
public abstract class QueueConfig {

  private static final Range<Integer> DELAY_RANGE = Range.closed(0, 900);
  private static final Range<Integer> MAXIMUM_MESSAGE_SIZE_RANGE = Range.closed(1_024, 262_144);
  private static final Range<Integer> WAIT_TIME_RANGE = Range.closed(0, 20);
  private static final Range<Integer> VISIBILITY_TIMEOUT_RANGE = Range.closed(0, 43_200);
  private static final Range<Integer> BATCH_SIZE_RANGE = Range.closed(0, 10);

  @Value.Default
  public int messageRetentionPeriodSeconds() {
    return 345_600;
  }

  @Value.Check
  protected void check() {
    Preconditions.checkState(DELAY_RANGE.contains(delaySeconds()));
    Preconditions.checkState(MAXIMUM_MESSAGE_SIZE_RANGE.contains(maxMessageSizeBytes()));
    Preconditions.checkState(WAIT_TIME_RANGE.contains(receiveMessageWaitTimeSeconds()));
    Preconditions.checkState(VISIBILITY_TIMEOUT_RANGE.contains(visibilityTimeoutSeconds()));
    Preconditions.checkState(BATCH_SIZE_RANGE.contains(batchSize()));
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
