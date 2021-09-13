package org.mlsk.ui.component.popup;

import javax.swing.*;
import java.util.concurrent.Callable;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static javax.swing.JOptionPane.ERROR_MESSAGE;

public final class ErrorPopup {

  private ErrorPopup() {
  }

  public static void showErrorPopup(String title, String errorMessage) {
    JOptionPane.showMessageDialog(null, errorMessage, title, ERROR_MESSAGE);
  }

  public static <Return> Return tryPopup(Callable<Return> callable, String action) {
    try {
      return callable.call();
    } catch (Exception exception) {
      return showErrorPopupAndRethrow(action, exception);
    }
  }

  public static void tryPopupVoid(Runnable runnable, String action) {
    try {
      runnable.run();
    } catch (Exception exception) {
      showErrorPopupAndRethrow(action, exception);
    }
  }

  private static <Return> Return showErrorPopupAndRethrow(String action, Exception exception) {
    Throwable mainException = ofNullable(exception.getCause()).orElse(exception);
    String exceptionClass = mainException.getClass().getSimpleName();

    String fullErrorMessage = format("Error while %s%n\tException:%n\t\t%s%n\tCause:%n\t\t%s", action, exceptionClass, exception.getMessage());

    showErrorPopup(exceptionClass, fullErrorMessage);

    throw new RuntimeException(mainException);
  }
}