package org.cirrus.infrastructure.task.function;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.cirrus.infrastructure.task.resource.CreateResourceOutput;
import org.cirrus.infrastructure.task.resource.ResourceType;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableAddQueueInput.class)
@JsonDeserialize(as = ImmutableAddQueueInput.class)
public abstract class AddQueueInput {

  public static Builder newBuilder() {
    return ImmutableAddQueueInput.newBuilder();
  }

  @Value.Derived
  public Map<ResourceType, CreateResourceOutput> getTypedOutputs() {
    return getOutputs().stream()
        .map(o -> new SimpleEntry<>(o.getType(), o))
        .collect(ImmutableMap.toImmutableMap(Entry::getKey, Entry::getValue));
  }

  public abstract List<CreateResourceOutput> getOutputs();

  public interface Builder {

    Builder addOutput(CreateResourceOutput element);

    Builder addAllOutputs(Iterable<? extends CreateResourceOutput> elements);

    Builder addOutputs(CreateResourceOutput... elements);

    AddQueueInput build();
  }
}
