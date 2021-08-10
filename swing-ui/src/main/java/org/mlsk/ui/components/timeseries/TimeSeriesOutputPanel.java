package org.mlsk.ui.components.timeseries;

import org.mlsk.service.model.timeseries.TimeSeries;

import javax.swing.*;

public class TimeSeriesOutputPanel extends JPanel {

  public void onTimeSeriesResult(TimeSeries initial, TimeSeries result, String title) {
    this.add(new TimeSeriesPlotPanel(initial, result, title));
  }

  public void onForecastAccuracyResult(Double forecastAccuracy) {
    String label = String.format("Forecast Accuracy: %s", forecastAccuracy) + "%";
    this.add(new JLabel(label));
  }
}
