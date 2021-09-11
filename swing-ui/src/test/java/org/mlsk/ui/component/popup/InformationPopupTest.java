package org.mlsk.ui.component.popup;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;

import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static org.mlsk.ui.component.popup.InformationPopup.showInfoPopup;

public class InformationPopupTest {

  @Test
  public void should_display_message_dialog_on_show_info_popup() {
    try (MockedStatic<JOptionPane> mockedStatic = Mockito.mockStatic(JOptionPane.class)) {

      showInfoPopup("title", "infoMessage");

      mockedStatic.verify(() -> JOptionPane.showMessageDialog(null, "infoMessage", "title", INFORMATION_MESSAGE));
    }
  }

}