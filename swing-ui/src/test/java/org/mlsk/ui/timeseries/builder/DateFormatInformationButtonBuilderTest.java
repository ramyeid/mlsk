package org.mlsk.ui.timeseries.builder;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;

import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.ui.timeseries.builder.DateFormatInformationButtonBuilder.buildDateFormatInformationButton;

public class DateFormatInformationButtonBuilderTest {

  @Test
  public void should_build_date_format_information_button() {

    JButton actual = buildDateFormatInformationButton();

    assertEquals("?", actual.getText());
  }

  @Test
  public void should_show_information_panel_on_click() {
    try (MockedStatic<JOptionPane> mockedStatic = Mockito.mockStatic(JOptionPane.class)) {
      JButton actual = buildDateFormatInformationButton();

      actual.doClick();

      mockedStatic.verify(() -> JOptionPane.showMessageDialog(null, buildExpectedText(), "Date Format information", INFORMATION_MESSAGE));
    }
  }

  private static String buildExpectedText() {
    return "Date format according to Java SimpleDate:\n" +
        "year: yy or yyyy\n" +
        "month: MM\n" +
        "day: dd\n" +
        "hour (0-12): hh\n" +
        "hour (0-23): HH\n" +
        "minute: mm\n" +
        "seconds: ss\n" +
        "milliseconds: SSS";
  }
}