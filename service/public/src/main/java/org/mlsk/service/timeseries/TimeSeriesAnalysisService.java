package org.mlsk.service.timeseries;

import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;

public interface TimeSeriesAnalysisService {

  TimeSeries forecast(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest);

  TimeSeries forecastVsActual(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest);

  Double computeForecastAccuracy(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest);

  TimeSeries predict(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest);
}
