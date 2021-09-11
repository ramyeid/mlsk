package org.mlsk.ui.configuration.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.ui.configuration.service.ConfigurationCommand;
import org.mlsk.ui.configuration.service.ConfigurationService;
import org.mlsk.ui.configuration.service.ConfigurationServiceException;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mlsk.ui.configuration.service.ConfigurationCommand.CANCEL;
import static org.mlsk.ui.configuration.service.ConfigurationCommand.SAVE;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConfigurationPanelTest {

  @Mock
  private ServiceConfigurationPanel serviceConfigurationPanel;
  @Mock
  private ConfigurationService configurationService;

  private ConfigurationPanel configurationPanel;

  @BeforeEach
  public void setUp() {
    configurationPanel = new ConfigurationPanel(serviceConfigurationPanel, configurationService);
  }

  @Test
  public void should_call_service_on_save() {
    ActionEvent actionEvent = mockActionEvent(SAVE);

    configurationPanel.actionPerformed(actionEvent);

    verify(configurationService).save(serviceConfigurationPanel);
  }

  @Test
  public void should_show_popup_if_save_fails() {
    try (MockedStatic<JOptionPane> mockedStatic = Mockito.mockStatic(JOptionPane.class)) {
      ActionEvent actionEvent = mockActionEvent(SAVE);
      doThrowExceptionOnSave(new ConfigurationServiceException("Error while saving", new NumberFormatException("numberFormatException")));

      try {
        configurationPanel.actionPerformed(actionEvent);
        fail("should fail since service save failed");

      } catch (Exception exception) {
        assertInstanceOf(RuntimeException.class, exception);
        assertInstanceOf(NumberFormatException.class, exception.getCause());
        assertEquals("java.lang.NumberFormatException: numberFormatException", exception.getMessage());
        mockedStatic.verify(() -> JOptionPane.showMessageDialog(null, "Unable to save configuration\nCause:\n\t\tError while saving", "ConfigurationServiceException: Unable to save configuration", ERROR_MESSAGE));
      }
    }
  }

  @Test
  public void should_call_service_on_restore() {
    ActionEvent actionEvent = mockActionEvent(CANCEL);

    configurationPanel.actionPerformed(actionEvent);

    verify(configurationService).restore(serviceConfigurationPanel);
  }


  private void doThrowExceptionOnSave(Exception exception) {
    doThrow(exception).when(configurationService).save(any());
  }

  private static ActionEvent mockActionEvent(ConfigurationCommand command) {
    ActionEvent actionEvent = mock(ActionEvent.class);
    when(actionEvent.getActionCommand()).thenReturn(command.getTitle());
    return actionEvent;
  }
}