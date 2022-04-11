package org.cirrus.infrastructure.app;

import org.cirrus.infrastructure.stack.DevelopmentStack;
import software.amazon.awscdk.App;
import software.amazon.awscdk.AppProps;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class CirrusApp {

  public static void main(final String[] args) {
    App app = new App(AppProps.builder().stackTraces(true).build());
    new DevelopmentStack(app, StackProps.builder().env(environment()).build());
    app.synth();
  }

  private static Environment environment() {
    return Environment.builder()
        .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
        .region(System.getenv("CDK_DEFAULT_REGION"))
        .build();
  }
}
