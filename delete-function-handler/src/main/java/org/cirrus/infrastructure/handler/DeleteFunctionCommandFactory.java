package org.cirrus.infrastructure.handler;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;

@AssistedFactory
interface DeleteFunctionCommandFactory {

  DeleteFunctionCommand create(@Assisted String functionId);
}
