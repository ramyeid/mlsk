package org.mlsk.service.model.timeseries;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public final class TimeSeriesUtils {

  private TimeSeriesUtils() {
  }

  static TimeSeries buildTimeSeriesPassengers() {
    TimeSeriesRow timeSeriesRow = buildTimeSeriesRow("1960-01", 1.0);
    TimeSeriesRow timeSeriesRow1 = buildTimeSeriesRow("1960-02", 2.0);
    TimeSeriesRow timeSeriesRow2 = buildTimeSeriesRow("1960-03", 3.0);
    TimeSeriesRow timeSeriesRow3 = buildTimeSeriesRow("1960-04", 4.0);
    TimeSeriesRow timeSeriesRow4 = buildTimeSeriesRow("1960-05", 5.0);
    List<TimeSeriesRow> timeSeriesRows = newArrayList(timeSeriesRow, timeSeriesRow1, timeSeriesRow2, timeSeriesRow3, timeSeriesRow4);
    return new TimeSeries(timeSeriesRows, "Date", "Passengers", "%Y-%m");
  }

  static TimeSeriesRow buildTimeSeriesRow(String date, Double value) {
    return new TimeSeriesRow(date, value);
  }

  static TimeSeries buildTimeSeriesPassengers1() {
    TimeSeriesRow timeSeriesRow = buildTimeSeriesRow("1960-06", 6.0);
    TimeSeriesRow timeSeriesRow1 = buildTimeSeriesRow("1961-07", 7.0);
    List<TimeSeriesRow> timeSeriesRows = newArrayList(timeSeriesRow, timeSeriesRow1);
    return new TimeSeries(timeSeriesRows, "Date", "Passengers", "%Y-%m");
  }
}
