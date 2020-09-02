package org.machinelearning.swissknife.model.timeseries;

import java.util.Objects;

public class TimeSeriesAnalysisRequest {

    private final TimeSeries timeSeries;
    private final int numberOfValues;

    public TimeSeriesAnalysisRequest(TimeSeries timeSeries, int numberOfValues) {
        this.timeSeries = timeSeries;
        this.numberOfValues = numberOfValues;
    }

    public TimeSeriesAnalysisRequest() {
        this(null, 0);
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
        return numberOfValues == that.numberOfValues &&
                Objects.equals(timeSeries, that.timeSeries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeSeries, numberOfValues);
    }

    @Override
    public String toString() {
        return "TimeSeriesAnalysisRequest{" +
                "timeSeries=" + timeSeries +
                ", numberOfValues=" + numberOfValues +
                '}';
    }
}
