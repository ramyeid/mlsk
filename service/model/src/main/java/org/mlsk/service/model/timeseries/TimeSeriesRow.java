package org.mlsk.service.model.timeseries;

import java.util.Objects;

public class TimeSeriesRow {

  private final String date;
  private final Double value;

  public TimeSeriesRow(String date, Double value) {
    this.date = date;
    this.value = value;
  }

  // Needed for deserialization from json
  public TimeSeriesRow() {
    this(null, 0.);
  }

  public String getDate() {
    return date;
  }

  public Double getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TimeSeriesRow that = (TimeSeriesRow) o;
    return Objects.equals(date, that.date) &&
        Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(date, value);
  }

  @Override
  public String toString() {
    return "TimeSeriesRow{" +
        "date='" + date + '\'' +
        ", value=" + value +
        '}';
  }
}
