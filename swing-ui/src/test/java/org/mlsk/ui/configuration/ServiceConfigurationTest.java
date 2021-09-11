package org.mlsk.ui.configuration;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;
import org.mlsk.lib.model.ServiceInformation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServiceConfigurationTest {

  @Test
  public void should_build_correct_service_configuration_given_parameters() throws ParseException {
    String enginePorts = "6766";

    ServiceConfiguration.buildServiceConfiguration("", "--service-port", enginePorts);

    ServiceInformation expectedServiceInformation = new ServiceInformation("localhost", enginePorts);
    assertEquals(expectedServiceInformation, ServiceConfiguration.getServiceInformation());
  }

  @Test
  public void should_build_correct_service_configuration_given_parameters_with_service_host() throws ParseException {
    String enginePorts = "6766";

    ServiceConfiguration.buildServiceConfiguration("", "--service-port", enginePorts, "--service-host", "myHost");

    ServiceInformation expectedServiceInformation = new ServiceInformation("myHost", enginePorts);
    assertEquals(expectedServiceInformation, ServiceConfiguration.getServiceInformation());
  }
}