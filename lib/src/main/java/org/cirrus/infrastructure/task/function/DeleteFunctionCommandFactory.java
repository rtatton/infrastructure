package org.cirrus.infrastructure.task.function;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;

@AssistedFactory
interface DeleteFunctionCommandFactory {

  DeleteFunctionCommand create(@Assisted String functionId);
}
