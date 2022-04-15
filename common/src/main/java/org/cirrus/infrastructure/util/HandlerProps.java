package org.cirrus.infrastructure.util;

import org.immutables.value.Value;

@Value.Immutable
public interface HandlerProps {

  static Builder builder() {
    return ImmutableHandlerProps.builder();
  }

  /** Name of the Lambda function handler class. */
  String handlerName();

  /** Fully-qualified path (package and class name) of the Lambda function handler. */
  String handlerPath();

  /** Root directory containing the build files and source code for the handler. */
  String handlerModule();

  interface Builder {

    Builder handlerName(String handlerName);

    Builder handlerPath(String handlerPath);

    Builder handlerModule(String handlerModule);

    HandlerProps build();
  }
}
