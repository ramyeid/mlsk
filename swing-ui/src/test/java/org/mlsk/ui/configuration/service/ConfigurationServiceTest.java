package org.mlsk.ui.configuration.service;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.ui.configuration.component.ServiceConfigurationPanel;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InvalidObjectException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mlsk.ui.setup.ServiceConfiguration.buildServiceConfiguration;
import static org.mlsk.ui.setup.ServiceConfiguration.getServiceInformation;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConfigurationServiceTest {

  @Mock
  private ServiceConfigurationPanel serviceConfigurationPanel;

  private ConfigurationService configurationService;

  @BeforeEach
  public void setUp() {
    configurationService = new ConfigurationService();
  }

  @Test
  public void should_restore_values() throws ParseException {
    buildServiceConfiguration("", "--service-host", "myLocalHost", "--service-port", "14912");

    configurationService.restore(serviceConfigurationPanel);

    InOrder inOrder = buildInOrder();
    inOrder.verify(serviceConfigurationPanel).setServiceHost("myLocalHost");
    inOrder.verify(serviceConfigurationPanel).setServicePort("14912");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_save_values() throws ParseException {
    buildServiceConfiguration("", "--service-host", "myLocalHost", "--service-port", "14912");
    onGetServiceHostReturn(serviceConfigurationPanel, "newHost");
    onGetServicePortReturn(serviceConfigurationPanel, "983612");

    configurationService.save(serviceConfigurationPanel);

    assertEquals(new ServiceInformation("newHost", 983612L), getServiceInformation());
  }

  @Test
  public void should_throw_exception_on_save_if_service_host_null_or_blank() {
    onGetServiceHostReturn(serviceConfigurationPanel, null);
    onGetServicePortReturn(serviceConfigurationPanel, "123");

    try {
      configurationService.save(serviceConfigurationPanel);
      fail("should fail since service host input is null");

    } catch (Exception exception) {
      assertOnConfigurationServiceException(exception, InvalidObjectException.class, "java.io.InvalidObjectException: The serviceHost value can not be null");
    }
  }

  @Test
  public void should_throw_exception_on_save_if_service_port_null_or_blank() {
    onGetServiceHostReturn(serviceConfigurationPanel, "newHost");
    onGetServicePortReturn(serviceConfigurationPanel, "     ");

    try {
      configurationService.save(serviceConfigurationPanel);
      fail("should fail since service port input is blank");

    } catch (Exception exception) {
      assertOnConfigurationServiceException(exception, InvalidObjectException.class, "java.io.InvalidObjectException: The servicePort value can not be null");
    }
  }

  @Test
  public void should_throw_exception_on_save_if_service_port_non_numeric() {
    onGetServiceHostReturn(serviceConfigurationPanel, "newHost");
    onGetServicePortReturn(serviceConfigurationPanel, "nonNumeric");

    try {
      configurationService.save(serviceConfigurationPanel);
      fail("should fail since service port input is non numeric");

    } catch (Exception exception) {
      assertOnConfigurationServiceException(exception, NumberFormatException.class, "java.lang.NumberFormatException: For input string: \"nonNumeric\"");
    }
  }

  private InOrder buildInOrder() {
    return inOrder(serviceConfigurationPanel);
  }

  private static void onGetServiceHostReturn(ServiceConfigurationPanel serviceConfigurationPanel, String serviceHost) {
    when(serviceConfigurationPanel.getServiceHost()).thenReturn(serviceHost);
  }

  private static void onGetServicePortReturn(ServiceConfigurationPanel serviceConfigurationPanel, String servicePort) {
    when(serviceConfigurationPanel.getServicePort()).thenReturn(servicePort);
  }

  private static void assertOnConfigurationServiceException(Exception exception, Class<? extends Exception> cause, String message) {
    assertInstanceOf(ConfigurationServiceException.class, exception);
    assertInstanceOf(cause, exception.getCause());
    assertEquals(message, exception.getMessage());
  }
}