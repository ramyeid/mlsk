package org.mlsk.service.timeseries;

import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;

public interface TimeSeriesAnalysisEngine {

  TimeSeries forecast(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest);

  Double computeForecastAccuracy(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest);

  TimeSeries predict(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest);
}
