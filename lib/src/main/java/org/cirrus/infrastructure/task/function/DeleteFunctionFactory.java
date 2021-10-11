package org.cirrus.infrastructure.task.function;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;

@AssistedFactory
interface DeleteFunctionFactory {

  DeleteFunction create(@Assisted String functionId);
}
