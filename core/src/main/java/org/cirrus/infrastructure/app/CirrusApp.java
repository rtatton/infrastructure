package org.cirrus.infrastructure.app;

import org.cirrus.infrastructure.stack.DevelopmentStack;
import software.amazon.awscdk.App;

public class CirrusApp {

  public static void main(final String[] args) {
    App app = new App();
    DevelopmentStack.create(app);
    app.synth();
  }
}
