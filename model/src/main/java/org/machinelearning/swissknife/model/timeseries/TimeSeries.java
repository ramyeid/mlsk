package org.machinelearning.swissknife.model.timeseries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TimeSeries {

  private final List<TimeSeriesRow> rows;
  private final String dateColumnName;
  private final String valueColumnName;
  private final String dateFormat;

  public TimeSeries(List<TimeSeriesRow> rows, String dateColumnName, String valueColumnName, String dateFormat) {
    this.rows = rows;
    this.dateColumnName = dateColumnName;
    this.valueColumnName = valueColumnName;
    this.dateFormat = dateFormat;
  }

  public TimeSeries() {
    this(new ArrayList<>(), "", "", "");
  }

  public static TimeSeries concat(TimeSeries firstTimeSeries, TimeSeries secondTimeSeries) {
    List<TimeSeriesRow> concatenatedRows = Stream.concat(firstTimeSeries.rows.stream(), secondTimeSeries.rows.stream())
        .collect(Collectors.toList());
    return new TimeSeries(concatenatedRows, firstTimeSeries.dateColumnName, firstTimeSeries.valueColumnName, firstTimeSeries.dateFormat);
  }

  public List<TimeSeriesRow> getRows() {
    return rows;
  }

  public String getDateColumnName() {
    return dateColumnName;
  }

  public String getValueColumnName() {
    return valueColumnName;
  }

  public String getDateFormat() {
    return dateFormat;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TimeSeries that = (TimeSeries) o;
    return Objects.equals(rows, that.rows) &&
        Objects.equals(dateColumnName, that.dateColumnName) &&
        Objects.equals(valueColumnName, that.valueColumnName) &&
        Objects.equals(dateFormat, that.dateFormat);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rows, dateColumnName, valueColumnName, dateFormat);
  }

  @Override
  public String toString() {
    return "TimeSeries{" +
        "rows=" + rows +
        ", dateColumnName='" + dateColumnName + '\'' +
        ", valueColumnName='" + valueColumnName + '\'' +
        ", dateFormat='" + dateFormat + '\'' +
        '}';
  }
}
