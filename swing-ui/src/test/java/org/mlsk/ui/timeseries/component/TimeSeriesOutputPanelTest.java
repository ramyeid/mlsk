package org.mlsk.ui.timeseries.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mockito.ArgumentMatcher;

import javax.swing.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class TimeSeriesOutputPanelTest {

  private TimeSeriesOutputPanel outputPanel;

  @BeforeEach
  public void setUp() {
    this.outputPanel = spy(new TimeSeriesOutputPanel());
  }

  @Test
  public void should_add_time_series_plot_panel_on_time_series_result() {

    outputPanel.onTimeSeriesResult(new TimeSeries(), new TimeSeries(), "myTitle");

    verify(outputPanel).add(any(TimeSeriesPlotPanel.class));
  }

  @Test
  public void should_add_label_on_forecast_accuracy_result() {

    outputPanel.onForecastAccuracyResult(13.412);

    verify(outputPanel).add(argThat(labelMatcher("Forecast Accuracy: 13.412%")));
  }

  private static ArgumentMatcher<JLabel> labelMatcher(String expectedMessage) {
    return actual -> actual.getText().equals(expectedMessage);
  }
}
