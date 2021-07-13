package org.mlsk.ui.components.utils;

import javax.swing.*;
import java.util.concurrent.Callable;

public class ErrorPopup {

  public static void showErrorPopup(String title, String errorMessage) {
    JOptionPane.showMessageDialog(null, errorMessage, title, JOptionPane.ERROR_MESSAGE);
  }

  public static <Return> Return tryPopup(Callable<Return> callable, String errorMessageIn) {
    try {
      return callable.call();
    } catch (Exception exception) {
      String errorMessage = String.format("%s\nCause:\n\t\t%s", errorMessageIn, exception.getMessage());
      String title = String.format("%s: %s", exception.getClass().getSimpleName(), errorMessageIn);
      showErrorPopup(title, errorMessage);
      throw new RuntimeException(exception.getCause() == null ? exception : exception.getCause());
    }
  }
}
