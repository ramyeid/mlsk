package org.mlsk.service.impl.timeseries.engine;

import com.google.common.annotations.VisibleForTesting;
import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.lib.rest.RestClient;
import org.mlsk.service.impl.timeseries.engine.exception.TimeSeriesAnalysisEngineRequestException;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.timeseries.TimeSeriesAnalysisEngine;
import org.springframework.web.client.HttpServerErrorException;

import static java.lang.String.format;
import static org.mlsk.service.timeseries.utils.TimeSeriesAnalysisConstants.*;

public class TimeSeriesAnalysisEngineClient implements TimeSeriesAnalysisEngine {

  private final RestClient restClient;

  public TimeSeriesAnalysisEngineClient(ServiceInformation serviceInformation) {
    this(new RestClient(serviceInformation));
  }

  @VisibleForTesting
  public TimeSeriesAnalysisEngineClient(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public TimeSeries forecast(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    try {
      return restClient.post(FORECAST_URL, timeSeriesAnalysisRequest, TimeSeries.class);
    } catch (HttpServerErrorException exception) {
      throw buildTimeSeriesAnalysisEngineRequestException(exception, "forecast");
    } catch (Exception exception) {
      throw buildTimeSeriesAnalysisEngineRequestException(exception, "forecast");
    }
  }

  @Override
  public Double computeForecastAccuracy(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    try {
      return restClient.post(FORECAST_ACCURACY_URL, timeSeriesAnalysisRequest, Double.class);
    } catch (HttpServerErrorException exception) {
      throw buildTimeSeriesAnalysisEngineRequestException(exception, "computeForecastAccuracy");
    } catch (Exception exception) {
      throw buildTimeSeriesAnalysisEngineRequestException(exception, "computeForecastAccuracy");
    }
  }

  @Override
  public TimeSeries predict(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    try {
      return restClient.post(PREDICT_URL, timeSeriesAnalysisRequest, TimeSeries.class);
    } catch (HttpServerErrorException exception) {
      throw buildTimeSeriesAnalysisEngineRequestException(exception, "predict");
    } catch (Exception exception) {
      throw buildTimeSeriesAnalysisEngineRequestException(exception, "predict");
    }
  }

  private static TimeSeriesAnalysisEngineRequestException buildTimeSeriesAnalysisEngineRequestException(Exception exception, String action) {
    return new TimeSeriesAnalysisEngineRequestException(format("Failed to post %s to engine", action), exception);
  }

  private static TimeSeriesAnalysisEngineRequestException buildTimeSeriesAnalysisEngineRequestException(HttpServerErrorException exception, String action) {
    return new TimeSeriesAnalysisEngineRequestException(format("Failed on post %s to engine: %s", action, exception.getResponseBodyAsString()), exception);
  }
}
