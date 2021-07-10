package org.mlsk.ui;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mlsk.service.model.ServiceInformation;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServiceConfigurationTest {

  @Test
  public void should_build_correct_service_configuration_given_parameters() throws ParseException {
    String enginePorts = "6766";

    ServiceConfiguration.buildServiceConfiguration("", "--service-port", enginePorts);

    ServiceInformation expectedServiceInformation = new ServiceInformation("localhost", enginePorts);
    Assertions.assertEquals(expectedServiceInformation, ServiceConfiguration.getServiceInformation());
  }
}