package org.cirrus.infrastructure.task.resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableDeleteResourceOutput.class)
@JsonDeserialize(as = ImmutableDeleteResourceOutput.class)
public abstract class DeleteResourceOutput {

  public static Builder newBuilder() {
    return ImmutableDeleteResourceOutput.newBuilder();
  }

  public abstract List<CreateResourceOutput> getOutputs();

  public interface Builder {

    Builder addOutput(CreateResourceOutput element);

    Builder addAllOutputs(Iterable<? extends CreateResourceOutput> elements);

    Builder addOutputs(CreateResourceOutput... elements);

    DeleteResourceOutput build();
  }
}
