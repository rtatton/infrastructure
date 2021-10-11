package org.cirrus.infrastructure.task.function;

import dagger.Component;
import java.util.function.Consumer;
import javax.inject.Singleton;

@Singleton
@Component(modules = {FunctionModule.class})
interface FunctionComponent {

  CreateFunction newCreateFunction();

  DeleteFunctionFactory newDeleteFunctionFactory();

  Consumer<Throwable> newLogger();
}
