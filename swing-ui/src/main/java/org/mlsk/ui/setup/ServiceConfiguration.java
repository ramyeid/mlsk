package org.mlsk.ui.setup;

import org.apache.commons.cli.*;
import org.mlsk.lib.model.Endpoint;

import static java.util.Optional.ofNullable;

public class ServiceConfiguration {

  private static ServiceConfiguration instance = null;

  private Endpoint endpoint;

  private ServiceConfiguration(Endpoint endpoint) {
    this.endpoint = endpoint;
  }

  public static synchronized Endpoint getEndpoint() {
    return instance.endpoint;
  }

  public static synchronized void setEndpoint(String host, Long port) {
    instance.endpoint = new Endpoint(host, port);
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
    Long port = Long.valueOf(cmd.getOptionValue("port"));
    String host = ofNullable(cmd.getOptionValue("host")).orElse("localhost");

    instance = new ServiceConfiguration(new Endpoint(host, port));
  }
}
