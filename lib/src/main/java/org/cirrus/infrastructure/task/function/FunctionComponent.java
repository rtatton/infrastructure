package org.cirrus.infrastructure.task.function;

import dagger.Component;
import java.util.function.Consumer;
import javax.inject.Singleton;

@Singleton
@Component(modules = {FunctionModule.class})
interface FunctionComponent {

  CreateFunction getCreateFunction();

  DeleteFunctionFactory getDeleteFunctionFactory();

  AddQueueFactory getAddQueueFactory();

  Consumer<Throwable> getLogger();
}
