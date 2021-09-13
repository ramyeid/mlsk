package org.mlsk.ui.component.popup;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import java.util.concurrent.Callable;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mlsk.ui.component.popup.ErrorPopup.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ErrorPopupTest {

  @Test
  public void should_display_message_dialog_on_show_error_popup() {
    try (MockedStatic<JOptionPane> mockedStatic = Mockito.mockStatic(JOptionPane.class)) {

      showErrorPopup("title", "errorMessage");

      verifyShowMessageDialog(mockedStatic, "title", "errorMessage");
    }
  }

  @Test
  public void should_call_callable_on_try_popup() {
    Callable<Integer> callable = () -> 1;

    Integer actual = tryPopup(callable, "errorMessage");

    assertEquals(1, actual);
  }

  @Test
  public void should_display_message_dialog_on_try_popup_with_exception() {
    try (MockedStatic<JOptionPane> mockedStatic = Mockito.mockStatic(JOptionPane.class)) {
      Callable<Integer> callable = () -> {
        throw new UnsupportedOperationException("exceptionMessage");
      };

      try {
        tryPopup(callable, "errorMessageParameter");

      } catch (Exception exception) {
        String title = "UnsupportedOperationException";
        String errorMessage = "Error while errorMessageParameter\n" +
            "\tException:\n\t\tUnsupportedOperationException\n" +
            "\tCause:\n\t\texceptionMessage";
        verifyShowMessageDialog(mockedStatic, title, errorMessage);
        assertOnException(exception, "exceptionMessage");
      }
    }
  }

  @Test
  public void should_call_runnable_on_try_popup_void() {
    Runnable runnable = mock(Runnable.class);

    tryPopupVoid(runnable, "errorMessage");

    verify(runnable).run();
  }


  @Test
  public void should_display_message_dialog_on_try_popup_void_with_exception() {
    try (MockedStatic<JOptionPane> mockedStatic = Mockito.mockStatic(JOptionPane.class)) {
      Runnable runnable = () -> {
        throw new UnsupportedOperationException("exceptionMessage");
      };

      try {
        tryPopupVoid(runnable, "errorMessageParameter");

      } catch (Exception exception) {
        String title = "UnsupportedOperationException";
        String errorMessage = "Error while errorMessageParameter" +
            "\n\tException:\n\t\tUnsupportedOperationException" +
            "\n\tCause:\n\t\texceptionMessage";
        verifyShowMessageDialog(mockedStatic, title, errorMessage);
        assertOnException(exception, "exceptionMessage");
      }
    }
  }

  private static void assertOnException(Exception exception, String exceptionMessage) {
    assertInstanceOf(RuntimeException.class, exception);
    assertInstanceOf(UnsupportedOperationException.class, exception.getCause());
    assertEquals("java.lang.UnsupportedOperationException: " + exceptionMessage, exception.getMessage());
  }

  private static void verifyShowMessageDialog(MockedStatic<JOptionPane> mockedStatic, String title, String errorMessage) {
    mockedStatic.verify(() -> JOptionPane.showMessageDialog(null, errorMessage, title, ERROR_MESSAGE));
  }
}