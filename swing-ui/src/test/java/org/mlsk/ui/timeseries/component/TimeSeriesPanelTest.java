package org.mlsk.ui.timeseries.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.service.timeseries.model.TimeSeriesModel;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;

import java.math.BigDecimal;

import static java.math.BigDecimal.valueOf;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
public class TimeSeriesPanelTest {

  @Mock
  private TimeSeriesInputPanel inputPanel;
  @Mock
  private TimeSeriesOutputPanel outputPanel;

  private TimeSeriesPanel timeSeriesPanel;

  @BeforeEach
  public void setUp() {
    this.timeSeriesPanel = new TimeSeriesPanel(inputPanel, outputPanel);
  }

  @Test
  public void should_remove_all_and_call_output_panel_on_results_with_time_series() {
    try (MockedStatic<SwingUtilities> mockedStatic = Mockito.mockStatic(SwingUtilities.class)) {
      TimeSeriesModel initial = new TimeSeriesModel();
      TimeSeriesModel result = new TimeSeriesModel();
      String title = "title";

      timeSeriesPanel.onResult(initial, result, title);

      InOrder inOrder = inOrder(inputPanel, outputPanel);
      inOrder.verify(outputPanel).removeAll();
      inOrder.verify(outputPanel).onTimeSeriesResult(initial, result, title);
      inOrder.verifyNoMoreInteractions();
      mockedStatic.verify(() -> SwingUtilities.updateComponentTreeUI(timeSeriesPanel));
    }
  }

  @Test
  public void should_remove_all_and_call_output_panel_on_results_with_double() {
    try (MockedStatic<SwingUtilities> mockedStatic = Mockito.mockStatic(SwingUtilities.class)) {
      TimeSeriesModel initial = new TimeSeriesModel();
      BigDecimal result = valueOf(123.);
      String title = "title";

      timeSeriesPanel.onResult(initial, result, title);

      InOrder inOrder = inOrder(inputPanel, outputPanel);
      inOrder.verify(outputPanel).removeAll();
      inOrder.verify(outputPanel).onForecastAccuracyResult(result.doubleValue());
      inOrder.verifyNoMoreInteractions();
      mockedStatic.verify(() -> SwingUtilities.updateComponentTreeUI(timeSeriesPanel));
    }
  }
}