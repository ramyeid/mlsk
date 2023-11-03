package org.mlsk.ui.timeseries.service;

import org.mlsk.api.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.timeseries.model.TimeSeriesModel;
import org.mlsk.ui.timeseries.service.client.TimeSeriesAnalysisServiceClient;
import org.mlsk.ui.utils.TriFunction;

import java.util.Locale;
import java.util.concurrent.Callable;

import static org.mlsk.ui.timeseries.service.TimeSeriesAnalysisCommand.FORECAST;

public class TimeSeriesAnalysisServiceCaller {

  private final TimeSeriesAnalysisServiceClient timeSeriesAnalysisServiceClient;
  private final TriFunction<TimeSeriesModel, Object, String> onResults;

  public TimeSeriesAnalysisServiceCaller(TimeSeriesAnalysisServiceClient timeSeriesAnalysisServiceClient,
                                         TriFunction<TimeSeriesModel, Object, String> onResults) {
    this.timeSeriesAnalysisServiceClient = timeSeriesAnalysisServiceClient;
    this.onResults = onResults;
  }

  public void callService(TimeSeriesAnalysisCommand command, TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequest) {
    try {
      Object result = retrieveMethod(command, timeSeriesAnalysisRequest).call();

      onResults.apply(timeSeriesAnalysisRequest.getTimeSeries(), result, getTimeSeriesTitle(command));
    } catch (Exception exception) {
      throw new TimeSeriesAnalysisServiceException(exception);
    }
  }

  private String getTimeSeriesTitle(TimeSeriesAnalysisCommand command) {
    switch (command) {
      case PREDICT:
      case FORECAST_ACCURACY:
        return command.getTitle().toLowerCase(Locale.ROOT);
      case FORECAST:
      case FORECAST_VS_ACTUAL:
        return FORECAST.getTitle().toLowerCase(Locale.ROOT);
      default:
        return null;
    }
  }

  private Callable<Object> retrieveMethod(TimeSeriesAnalysisCommand command, TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequest) {
    switch (command) {
      case PREDICT:
        return () -> timeSeriesAnalysisServiceClient.predict(timeSeriesAnalysisRequest);
      case FORECAST:
        return () -> timeSeriesAnalysisServiceClient.forecast(timeSeriesAnalysisRequest);
      case FORECAST_VS_ACTUAL:
        return () -> timeSeriesAnalysisServiceClient.forecastVsActual(timeSeriesAnalysisRequest);
      case FORECAST_ACCURACY:
        return () -> timeSeriesAnalysisServiceClient.computeForecastAccuracy(timeSeriesAnalysisRequest);
      default:
        return () -> null;
    }
  }
}
