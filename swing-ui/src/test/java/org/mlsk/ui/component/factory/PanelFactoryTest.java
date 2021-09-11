package org.mlsk.ui.component.factory;

import org.junit.jupiter.api.Test;
import org.mlsk.ui.component.panel.EmptyPanel;
import org.mlsk.ui.timeseries.component.TimeSeriesPanel;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mlsk.ui.component.factory.MainCommand.EMPTY;
import static org.mlsk.ui.component.factory.MainCommand.TIME_SERIES_ANALYSIS;
import static org.mlsk.ui.component.factory.PanelFactory.buildPanel;

public class PanelFactoryTest {

  @Test
  public void should_build_time_series_panel() {

    JPanel actual = buildPanel(TIME_SERIES_ANALYSIS);

    assertInstanceOf(TimeSeriesPanel.class, actual);
  }

  @Test
  public void should_return_empty_panel_if_no_panel_for_command() {

    JPanel actual = buildPanel(EMPTY);

    assertInstanceOf(EmptyPanel.class, actual);
  }
}