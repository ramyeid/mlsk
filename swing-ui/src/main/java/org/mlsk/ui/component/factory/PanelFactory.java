package org.mlsk.ui.component.factory;

import org.mlsk.ui.component.panel.EmptyPanel;
import org.mlsk.ui.timeseries.component.TimeSeriesPanel;

import javax.swing.*;

public final class PanelFactory {

  private PanelFactory() {
  }

  public static JPanel buildPanel(MainCommand command) {
    switch (command) {
      case TIME_SERIES_ANALYSIS:
        return new TimeSeriesPanel();
      default:
        return new EmptyPanel();
    }
  }

}
