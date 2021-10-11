package org.cirrus.infrastructure.task.resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import java.util.Map;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableDeleteResourceInput.class)
@JsonDeserialize(as = ImmutableDeleteResourceInput.class)
public abstract class DeleteResourceInput {

  public static Builder newBuilder() {
    return ImmutableDeleteResourceInput.newBuilder();
  }

  public abstract List<CreateResourceOutput> getOutputs();

  @Value.Derived
  public Map<ResourceType, CreateResourceOutput> getTypedOutputs() {
    return null;
  }

  public interface Builder {

    Builder addOutput(CreateResourceOutput element);

    Builder addAllOutputs(Iterable<? extends CreateResourceOutput> elements);

    Builder addOutputs(CreateResourceOutput... elements);

    DeleteResourceInput build();
  }
}
