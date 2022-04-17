package org.cirrus.infrastructure.util;

import java.util.function.Function;
import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.IConstruct;

public final class Outputs {

  private Outputs() {}

  public static <T extends IConstruct> T output(
      Construct scope, String id, T construct, Function<T, String> getValue) {
    CfnOutput.Builder.create(scope, id).value(getValue.apply(construct)).build();
    return construct;
  }
}
