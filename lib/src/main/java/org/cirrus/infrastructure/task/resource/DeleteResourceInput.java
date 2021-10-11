package org.cirrus.infrastructure.task.resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableDeleteResourceInput.class)
@JsonDeserialize(as = ImmutableDeleteResourceInput.class)
public abstract class DeleteResourceInput {

  public static Builder newBuilder() {
    return ImmutableDeleteResourceInput.newBuilder();
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

    DeleteResourceInput build();
  }
}
