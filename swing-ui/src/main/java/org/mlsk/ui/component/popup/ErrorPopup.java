package org.mlsk.ui.component.popup;

import javax.swing.*;
import java.util.concurrent.Callable;

import static java.lang.String.format;
import static javax.swing.JOptionPane.ERROR_MESSAGE;

public final class ErrorPopup {

  private ErrorPopup() {
  }

  public static void showErrorPopup(String title, String errorMessage) {
    JOptionPane.showMessageDialog(null, errorMessage, title, ERROR_MESSAGE);
  }

  public static <Return> Return tryPopup(Callable<Return> callable, String errorMessageIn) {
    try {
      return callable.call();
    } catch (Exception exception) {
      return showErrorPopupAndRethrow(errorMessageIn, exception);
    }
  }

  public static void tryPopupVoid(Runnable runnable, String errorMessageIn) {
    try {
      runnable.run();
    } catch (Exception exception) {
      showErrorPopupAndRethrow(errorMessageIn, exception);
    }
  }

  private static <Return> Return showErrorPopupAndRethrow(String errorMessageIn, Exception exception) {
    String errorMessage = format("%s%nCause:%n\t\t%s", errorMessageIn, exception.getMessage());
    String title = format("%s: %s", exception.getClass().getSimpleName(), errorMessageIn);
    showErrorPopup(title, errorMessage);
    throw new RuntimeException(exception.getCause() == null ? exception : exception.getCause());
  }
}
