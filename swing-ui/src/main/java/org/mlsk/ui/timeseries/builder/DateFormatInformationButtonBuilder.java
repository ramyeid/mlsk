package org.mlsk.ui.timeseries.builder;

import javax.swing.*;
import java.util.StringJoiner;

import static org.mlsk.ui.component.builder.JButtonBuilder.buildJButton;
import static org.mlsk.ui.component.popup.InformationPopup.showInfoPopup;

public final class DateFormatInformationButtonBuilder {

  private DateFormatInformationButtonBuilder() {
  }

  public static JButton buildDateFormatInformationButton() {
    return buildJButton("?", e -> {
      StringJoiner stringBuilder = new StringJoiner("\n");
      stringBuilder.add("Date format according to Java SimpleDate:");
      stringBuilder.add("year: yy or yyyy");
      stringBuilder.add("month: MM");
      stringBuilder.add("day: dd");
      stringBuilder.add("hour (0-12): hh");
      stringBuilder.add("hour (0-23): HH");
      stringBuilder.add("minute: mm");
      stringBuilder.add("seconds: ss");
      stringBuilder.add("milliseconds: SSS");

      showInfoPopup("Date Format information", stringBuilder.toString());
    });
  }
}
