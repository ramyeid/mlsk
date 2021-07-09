package org.machinelearning.swissknife.ui;

import org.apache.commons.cli.ParseException;
import org.machinelearning.swissknife.ui.components.startup.MainFrame;

import static org.machinelearning.swissknife.ui.ServiceConfiguration.buildServiceConfiguration;

public class Application {

  public static void main(String[] args) throws ParseException {
    buildServiceConfiguration(args);
    new MainFrame();
  }
}
