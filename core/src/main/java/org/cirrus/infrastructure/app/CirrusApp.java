package org.cirrus.infrastructure.app;

import org.cirrus.infrastructure.stack.DevelopmentStack;
import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.AppProps;
import software.amazon.awscdk.core.Environment;
import software.amazon.awscdk.core.StackProps;

public class CirrusApp {

  public static void main(final String[] args) {
    App app = createApp();
    createStacks(app);
    app.synth();
  }

  private static App createApp() {
    return new App(AppProps.builder().stackTraces(true).build());
  }

  private static void createStacks(App scope) {
    new DevelopmentStack(scope, StackProps.builder().env(environment()).build());
  }

  private static Environment environment() {
    return Environment.builder()
        .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
        .region(System.getenv("CDK_DEFAULT_REGION"))
        .build();
  }
}
