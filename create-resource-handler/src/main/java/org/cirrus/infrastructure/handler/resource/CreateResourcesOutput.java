package org.cirrus.infrastructure.handler.resource;

import com.google.common.collect.ImmutableMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.cirrus.infrastructure.util.Resource;
import org.immutables.value.Value;

@Value.Immutable
public abstract class CreateResourcesOutput {

  public static Builder newBuilder() {
    return ImmutableCreateResourcesOutput.newBuilder();
  }

  @Value.Derived
  public Map<Resource, CreateResourceOutput> getTypedOutputs() {
    return getOutputs().stream()
        .map(output -> new SimpleEntry<>(output.getType(), output))
        .collect(ImmutableMap.toImmutableMap(Entry::getKey, Entry::getValue));
  }

  public abstract List<CreateResourceOutput> getOutputs();

  public interface Builder {

    Builder addOutput(CreateResourceOutput element);

    Builder addAllOutputs(Iterable<? extends CreateResourceOutput> elements);

    Builder addOutputs(CreateResourceOutput... elements);

    CreateResourcesOutput build();
  }
}
