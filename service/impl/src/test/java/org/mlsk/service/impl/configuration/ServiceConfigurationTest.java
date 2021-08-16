package org.mlsk.service.impl.configuration;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;
import org.mlsk.lib.model.ServiceInformation;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.configuration.ServiceConfiguration.*;

public class ServiceConfigurationTest {

  @Test
  public void should_build_correct_service_configuration_given_parameters() throws ParseException {
    String enginePorts = "6767,6768";
    String logsPath = "LogsPath";
    String enginePath = "EnginePath";

    buildServiceConfiguration("", "--engine-ports", enginePorts, "--logs-path", logsPath, "-engine-path", enginePath);

    ServiceInformation serviceInformation1 = new ServiceInformation("localhost", "6767");
    ServiceInformation serviceInformation2 = new ServiceInformation("localhost", "6768");
    assertEquals(newArrayList(serviceInformation1, serviceInformation2), getEnginesServiceInformation());
    assertEquals(logsPath, getLogsPath());
    assertEquals(enginePath, getEnginePath());
  }
}