package org.machinelearning.swissknife.service;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.machinelearning.swissknife.service.ServiceConfiguration.*;

class ServiceConfigurationTest {

  @Test
  public void should_build_correct_service_configuration_given_parameters() throws ParseException {
    String enginePorts = "6767,6768";
    String logsPath = "LogsPath";
    String enginePath = "EnginePath";

    buildServiceConfiguration("", "--engine-ports", enginePorts, "--logs-path", logsPath, "-engine-path", enginePath);

    assertEquals(asList("6767", "6768"), getEnginePorts());
    assertEquals(logsPath, getLogsPath());
    assertEquals(enginePath, getEnginePath());
  }
}