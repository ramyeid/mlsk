package org.mlsk.ui.timeseries.component;

import org.mlsk.api.timeseries.model.TimeSeriesModel;

import javax.swing.*;

import static java.lang.String.format;
import static org.mlsk.ui.component.builder.JLabelBuilder.buildJLabel;

public class TimeSeriesOutputPanel extends JPanel {

  public void onTimeSeriesResult(TimeSeriesModel initial, TimeSeriesModel result, String title) {
    this.add(new TimeSeriesPlotPanel(initial, result, title));
  }

  public void onForecastAccuracyResult(Double forecastAccuracy) {
    String label = format("Forecast Accuracy: %s", forecastAccuracy) + "%";
    this.add(buildJLabel(label));
  }
}
