package org.mlsk.ui.configuration.service;

import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.ui.configuration.component.ServiceConfigurationPanel;
import org.mlsk.ui.setup.ServiceConfiguration;

import java.io.InvalidObjectException;

import static java.lang.String.format;

public class ConfigurationService {

  public void restore(ServiceConfigurationPanel serviceConfigurationPanel) {
    restoreServiceConfiguration(serviceConfigurationPanel);
  }

  public void save(ServiceConfigurationPanel serviceConfigurationPanel) {
    try {
      saveServiceConfiguration(serviceConfigurationPanel);
    } catch (Exception exception) {
      throw new ConfigurationServiceException(format("Error while saving configuration: %s", exception.getMessage()), exception);
    }
  }

  private static void saveServiceConfiguration(ServiceConfigurationPanel serviceConfigurationPanel) throws Exception {
    String serviceHost = serviceConfigurationPanel.getServiceHost();
    String servicePort = serviceConfigurationPanel.getServicePort();

    throwExceptionIfNullOrEmpty(servicePort, "servicePort");
    throwExceptionIfNullOrEmpty(serviceHost, "serviceHost");

    ServiceConfiguration.setServiceInformation(serviceHost, Long.parseLong(servicePort));
  }

  private static void restoreServiceConfiguration(ServiceConfigurationPanel serviceConfigurationPanel) {
    ServiceInformation serviceInformation = ServiceConfiguration.getServiceInformation();

    serviceConfigurationPanel.setServiceHost(serviceInformation.getHost());
    serviceConfigurationPanel.setServicePort(String.valueOf(serviceInformation.getPort()));
  }

  private static void throwExceptionIfNullOrEmpty(String value, String valueDescription) throws InvalidObjectException {
    if (value == null || value.isBlank()) {
      throw new InvalidObjectException(format("The %s value can not be null", valueDescription));
    }
  }
}
