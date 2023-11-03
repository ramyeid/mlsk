package org.mlsk.ui.configuration.service;

import org.mlsk.lib.model.Endpoint;
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
      throw new ConfigurationServiceException(exception);
    }
  }

  private static void saveServiceConfiguration(ServiceConfigurationPanel serviceConfigurationPanel) throws Exception {
    String serviceHost = serviceConfigurationPanel.getServiceHost();
    String servicePort = serviceConfigurationPanel.getServicePort();

    throwExceptionIfNullOrEmpty(servicePort, "servicePort");
    throwExceptionIfNullOrEmpty(serviceHost, "serviceHost");

    ServiceConfiguration.setEndpoint(serviceHost, Long.parseLong(servicePort));
  }

  private static void restoreServiceConfiguration(ServiceConfigurationPanel serviceConfigurationPanel) {
    Endpoint endpoint = ServiceConfiguration.getEndpoint();

    serviceConfigurationPanel.setServiceHost(endpoint.getHost());
    serviceConfigurationPanel.setServicePort(String.valueOf(endpoint.getPort()));
  }

  private static void throwExceptionIfNullOrEmpty(String value, String valueDescription) throws InvalidObjectException {
    if (value == null || value.isBlank()) {
      throw new InvalidObjectException(format("The %s value can not be null", valueDescription));
    }
  }
}
