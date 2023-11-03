package org.mlsk.ui.setup;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;
import org.mlsk.lib.model.Endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServiceConfigurationTest {

  @Test
  public void should_build_correct_service_configuration_given_parameters() throws ParseException {
    String enginePorts = "6766";

    ServiceConfiguration.buildServiceConfiguration("", "--service-port", enginePorts);

    Endpoint expectedEndpoint = new Endpoint("localhost", 6766L);
    assertEquals(expectedEndpoint, ServiceConfiguration.getEndpoint());
  }

  @Test
  public void should_build_correct_service_configuration_given_parameters_with_service_host() throws ParseException {
    String enginePorts = "6766";

    ServiceConfiguration.buildServiceConfiguration("", "--service-port", enginePorts, "--service-host", "myHost");

    Endpoint expectedEndpoint = new Endpoint("myHost", 6766L);
    assertEquals(expectedEndpoint, ServiceConfiguration.getEndpoint());
  }
}