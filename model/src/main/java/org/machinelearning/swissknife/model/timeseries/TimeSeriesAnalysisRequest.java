package org.machinelearning.swissknife.model.timeseries;

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
}
