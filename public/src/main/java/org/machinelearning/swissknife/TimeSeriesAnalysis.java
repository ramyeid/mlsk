package org.machinelearning.swissknife;

import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;

public interface TimeSeriesAnalysis {

    TimeSeries forecast(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest);

    Double computeForecastAccuracy(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest);

    TimeSeries predict(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest);
}
