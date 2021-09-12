package org.mlsk.ui.configuration.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServiceConfigurationPanelTest {

  @Mock
  private JTextField serviceHostValue;
  @Mock
  private JTextField servicePortValue;

  private ServiceConfigurationPanel serviceConfigurationPanel;

  @BeforeEach
  public void setUp() {
    serviceConfigurationPanel = new ServiceConfigurationPanel(serviceHostValue, servicePortValue);
  }

  @Test
  public void should_retrieve_text_field_value_on_get_host() {
    onGetTextReturn(serviceHostValue, "localhost");

    String actual = serviceConfigurationPanel.getServiceHost();

    assertEquals("localhost", actual);
  }

  @Test
  public void should_set_text_field_value_on_set_host() {
    String serviceHost = "myLocalHost";

    serviceConfigurationPanel.setServiceHost(serviceHost);

    InOrder inOrder = buildInOrder();
    inOrder.verify(serviceHostValue).setText(serviceHost);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_retrieve_text_field_value_on_get_port() {
    onGetTextReturn(servicePortValue, "123");

    String actual = serviceConfigurationPanel.getServicePort();

    assertEquals("123", actual);
  }

  @Test
  public void should_set_text_field_value_on_set_port() {
    String servicePort = "6767";

    serviceConfigurationPanel.setServicePort(servicePort);

    InOrder inOrder = buildInOrder();
    inOrder.verify(servicePortValue).setText(servicePort);
    inOrder.verifyNoMoreInteractions();
  }

  private InOrder buildInOrder() {
    return inOrder(serviceHostValue, servicePortValue);
  }

  private static void onGetTextReturn(JTextField textField, String text) {
    when(textField.getText()).thenReturn(text);
  }
}