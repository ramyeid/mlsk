package org.mlsk.service.impl.setup;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;
import org.mlsk.lib.model.Endpoint;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.setup.ServiceConfiguration.*;

public class ServiceConfigurationTest {

  @Test
  public void should_build_correct_service_configuration_given_parameters() throws ParseException {
    String enginePorts = "6767,6768";
    String logsPath = "LogsPath";
    String enginePath = "EnginePath";

    buildServiceConfiguration("", "--engine-ports", enginePorts, "--logs-path", logsPath, "-engine-path", enginePath);

    Endpoint endpoint1 = new Endpoint("localhost", 6767L);
    Endpoint endpoint2 = new Endpoint("localhost", 6768L);
    assertEquals(newArrayList(endpoint1, endpoint2), getEngineEndpoints());
    assertEquals(logsPath, getLogsPath());
    assertEquals(enginePath, getEnginePath());
  }
}