package org.mlsk.service.impl.setup;

import org.apache.commons.cli.*;
import org.mlsk.lib.model.Endpoint;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ServiceConfiguration {

  private static ServiceConfiguration instance = null;

  private final String logsPath;
  private final String enginePath;
  private final List<String> enginePorts;

  private ServiceConfiguration(String logsPath, String enginePath, List<String> enginePorts) {
    this.logsPath = logsPath;
    this.enginePath = enginePath;
    this.enginePorts = enginePorts;
  }

  public static String getLogsPath() {
    return instance.logsPath;
  }

  public static String getEnginePath() {
    return instance.enginePath;
  }

  public static List<Endpoint> getEngineEndpoints() {
    return instance.enginePorts.stream()
        .map(Long::parseLong)
        .map(ServiceConfiguration::buildEndpoint)
        .collect(toList());
  }

  public static void buildServiceConfiguration(String... args) throws ParseException {
    Option enginePortsOption = new Option("enginePorts", "engine-ports", true, "Ports of the engine to be launched");
    Option logsPathOption = new Option("logsPath", "logs-path", true, "absolute path towards Logs");
    Option enginePathOption = new Option("enginePath", "engine-path", true, "absolute path towards engine_server.py");
    CommandLineParser parser = new DefaultParser();

    enginePortsOption.setRequired(true);
    logsPathOption.setRequired(true);
    enginePathOption.setRequired(true);

    Options options = new Options();
    options.addOption(enginePortsOption);
    options.addOption(logsPathOption);
    options.addOption(enginePathOption);

    CommandLine cmd = parser.parse(options, args);

    String logsPath = cmd.getOptionValue("logsPath");
    String enginePath = cmd.getOptionValue("enginePath");
    List<String> enginePorts = Arrays
        .stream(cmd.getOptionValue("enginePorts").split(","))
        .map(String::trim)
        .collect(toList());

    instance = new ServiceConfiguration(logsPath, enginePath, enginePorts);
  }

  private static Endpoint buildEndpoint(Long port) {
    return new Endpoint("localhost", port);
  }
}
