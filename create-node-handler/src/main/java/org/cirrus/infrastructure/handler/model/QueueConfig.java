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

  @Value.Check
  protected void check() {
    Preconditions.checkState(DELAY_RANGE.contains(getDelayInSeconds()));
    Preconditions.checkState(MAXIMUM_MESSAGE_SIZE_RANGE.contains(getMaximumMessageSizeInBytes()));
    Preconditions.checkState(WAIT_TIME_RANGE.contains(getReceiveMessageWaitTimeInSeconds()));
    Preconditions.checkState(VISIBILITY_TIMEOUT_RANGE.contains(getVisibilityTimeoutInSeconds()));
    Preconditions.checkState(BATCH_SIZE_RANGE.contains(getBatchSize()));
  }

  @Value.Default
  public int getDelayInSeconds() {
    return 0;
  }

  @Value.Default
  public int getMaximumMessageSizeInBytes() {
    return 262_144;
  }

  @Value.Default
  public int getReceiveMessageWaitTimeInSeconds() {
    return 0;
  }

  @Value.Default
  public int getVisibilityTimeoutInSeconds() {
    return 30;
  }

  @Value.Default
  public int getBatchSize() {
    return 10;
  }

  @Value.Default
  public int getMessageRetentionPeriodInSeconds() {
    return 345_600;
  }

  public interface Builder {
    QueueConfig build();

    Builder setDelayInSeconds(int delay);

    Builder setMaximumMessageSizeInBytes(int maximumSize);

    Builder setMessageRetentionPeriodInSeconds(int retentionPeriod);

    Builder setReceiveMessageWaitTimeInSeconds(int receiveWaitTime);

    Builder setVisibilityTimeoutInSeconds(int visibilityTimeout);

    Builder setBatchSize(int batchSize);
  }
}
