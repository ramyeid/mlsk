package org.mlsk.ui.component.popup;

import javax.swing.*;

import static javax.swing.JOptionPane.INFORMATION_MESSAGE;

public final class InformationPopup {

  private InformationPopup() {
  }

  public static void showInfoPopup(String title, String errorMessage) {
    JOptionPane.showMessageDialog(null, errorMessage, title, INFORMATION_MESSAGE);
  }
}
