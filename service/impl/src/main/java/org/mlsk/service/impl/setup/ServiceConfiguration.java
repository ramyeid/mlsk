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
  private final String logLevel;
  private final String engineLogLevel;

  private ServiceConfiguration(String logsPath, String enginePath, List<String> enginePorts, String logLevel, String engineLogLevel) {
    this.logsPath = logsPath;
    this.enginePath = enginePath;
    this.enginePorts = enginePorts;
    this.logLevel = logLevel;
    this.engineLogLevel = engineLogLevel;
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

  public static String getLogLevel() {
    return instance.logLevel;
  }

  public static String getEngineLogLevel() {
    return instance.engineLogLevel;
  }

  public static void buildServiceConfiguration(String... args) throws ParseException {
    Option enginePortsOption = new Option("enginePorts", "engine-ports", true, "Ports of the engine to be launched");
    Option logsPathOption = new Option("logsPath", "logs-path", true, "Absolute path towards Logs");
    Option enginePathOption = new Option("enginePath", "engine-path", true, "Absolute path towards engine_server.py");
    Option logLevelOption = new Option("logLevelOption", "log-level", true, "Log level of service logger");
    Option engineLogLevelOption = new Option("engineLogLevelOption", "engine-log-level", true, "Log level of engines logger");
    CommandLineParser parser = new DefaultParser();

    enginePortsOption.setRequired(true);
    logsPathOption.setRequired(true);
    enginePathOption.setRequired(true);
    logLevelOption.setRequired(true);
    engineLogLevelOption.setRequired(true);

    Options options = new Options();
    options.addOption(enginePortsOption);
    options.addOption(logsPathOption);
    options.addOption(enginePathOption);
    options.addOption(logLevelOption);
    options.addOption(engineLogLevelOption);

    CommandLine cmd = parser.parse(options, args);

    String logsPath = cmd.getOptionValue("logsPath");
    String enginePath = cmd.getOptionValue("enginePath");
    List<String> enginePorts = Arrays
        .stream(cmd.getOptionValue("enginePorts").split(","))
        .map(String::trim)
        .collect(toList());
    String logLevel =  cmd.getOptionValue("logLevelOption");
    String engineLogLevel =  cmd.getOptionValue("engineLogLevelOption");

    instance = new ServiceConfiguration(logsPath, enginePath, enginePorts, logLevel, engineLogLevel);
  }

  private static Endpoint buildEndpoint(Long port) {
    return new Endpoint("localhost", port);
  }
}
