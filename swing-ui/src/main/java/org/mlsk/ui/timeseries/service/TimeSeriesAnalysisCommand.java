package org.mlsk.ui.timeseries.service;

import java.util.Arrays;

public enum TimeSeriesAnalysisCommand {

  PREDICT("Predict"),
  FORECAST("Forecast"),
  FORECAST_VS_ACTUAL("Forecast vs Actual"),
  FORECAST_ACCURACY("Forecast Accuracy");

  private final String title;

  TimeSeriesAnalysisCommand(String title) {
    this.title = title;
  }

  public String getTitle() {
    return this.title;
  }

  public static TimeSeriesAnalysisCommand fromString(String title) {
    return Arrays.stream(TimeSeriesAnalysisCommand.values())
        .filter(t -> t.title.equalsIgnoreCase(title))
        .findFirst()
        .orElse(null);
  }
}
