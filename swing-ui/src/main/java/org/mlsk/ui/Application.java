package org.mlsk.ui;

import org.apache.commons.cli.ParseException;
import org.mlsk.ui.component.MainFrame;

import static org.mlsk.ui.configuration.ServiceConfiguration.buildServiceConfiguration;

public class Application {

  public static void main(String[] args) throws ParseException {
    buildServiceConfiguration(args);
    new MainFrame();
  }
}
