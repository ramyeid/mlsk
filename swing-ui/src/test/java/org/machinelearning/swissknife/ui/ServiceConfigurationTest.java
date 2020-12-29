package org.machinelearning.swissknife.ui;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;
import org.machinelearning.swissknife.model.ServiceInformation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.machinelearning.swissknife.ui.ServiceConfiguration.buildServiceConfiguration;
import static org.machinelearning.swissknife.ui.ServiceConfiguration.getServiceInformation;

class ServiceConfigurationTest {

    @Test
    public void should_build_correct_service_configuration_given_parameters() throws ParseException {
        String enginePorts = "6766";

        buildServiceConfiguration("", "--service-port", enginePorts);

        ServiceInformation expectedServiceInformation = new ServiceInformation("localhost", enginePorts);
        assertEquals(expectedServiceInformation, getServiceInformation());
    }
}