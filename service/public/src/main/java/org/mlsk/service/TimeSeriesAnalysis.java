package org.mlsk.service;

import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;

public interface TimeSeriesAnalysis {

  TimeSeries forecast(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest);

  TimeSeries forecastVsActual(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest);

  Double computeForecastAccuracy(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest);

  TimeSeries predict(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest);
}
