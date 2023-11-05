package org.mlsk.service.model.timeseries;

import java.util.Objects;

public class TimeSeriesAnalysisRequest {

  private final long requestId;
  private final TimeSeries timeSeries;
  private final int numberOfValues;

  public TimeSeriesAnalysisRequest(long requestId, TimeSeries timeSeries, int numberOfValues) {
    this.requestId = requestId;
    this.timeSeries = timeSeries;
    this.numberOfValues = numberOfValues;
  }

  // Needed for deserialization from json
  public TimeSeriesAnalysisRequest() {
    this(0L, null, 0);
  }

  public long getRequestId() {
    return requestId;
  }

  public TimeSeries getTimeSeries() {
    return timeSeries;
  }

  public int getNumberOfValues() {
    return numberOfValues;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TimeSeriesAnalysisRequest that = (TimeSeriesAnalysisRequest) o;
    return requestId == that.requestId &&
        numberOfValues == that.numberOfValues &&
        Objects.equals(timeSeries, that.timeSeries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, timeSeries, numberOfValues);
  }

  @Override
  public String toString() {
    return "TimeSeriesAnalysisRequest{" +
        "requestId=" + requestId +
        ", timeSeries=" + timeSeries +
        ", numberOfValues=" + numberOfValues +
        '}';
  }
}
