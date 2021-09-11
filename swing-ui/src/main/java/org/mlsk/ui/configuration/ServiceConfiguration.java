package org.mlsk.ui.configuration;

import org.apache.commons.cli.*;
import org.mlsk.lib.model.ServiceInformation;

import static java.util.Optional.ofNullable;

public class ServiceConfiguration {

  private static ServiceConfiguration instance = null;

  private final ServiceInformation serviceInformation;

  private ServiceConfiguration(ServiceInformation serviceInformation) {
    this.serviceInformation = serviceInformation;
  }

  public static ServiceInformation getServiceInformation() {
    return instance.serviceInformation;
  }

  public static void buildServiceConfiguration(String... args) throws ParseException {
    Option servicePortOption = new Option("port", "service-port", true, "Port of the service");
    servicePortOption.setRequired(true);

    Option serviceHostOption = new Option("host", "service-host", true, "Host of the service");
    serviceHostOption.setRequired(false);

    CommandLineParser parser = new DefaultParser();
    Options options = new Options();
    options.addOption(servicePortOption);
    options.addOption(serviceHostOption);
    CommandLine cmd;

    cmd = parser.parse(options, args);
    String port = cmd.getOptionValue("port");
    String host = ofNullable(cmd.getOptionValue("host")).orElse("localhost");

    instance = new ServiceConfiguration(new ServiceInformation(host, port));
  }
}
