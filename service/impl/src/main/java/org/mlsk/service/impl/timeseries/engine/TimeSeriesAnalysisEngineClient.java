package org.mlsk.service.impl.timeseries.engine;

import com.google.common.annotations.VisibleForTesting;
import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.lib.rest.RestClient;
import org.mlsk.service.impl.timeseries.engine.exceptions.TimeSeriesAnalysisEngineRequestException;
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
      String engineException = format("Failed on post forecast to engine: %s", exception.getResponseBodyAsString());
      throw new TimeSeriesAnalysisEngineRequestException(engineException, exception);
    } catch (Exception exception) {
      throw new TimeSeriesAnalysisEngineRequestException("Failed to post forecast to engine", exception);
    }
  }

  @Override
  public Double computeForecastAccuracy(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    try {
      return restClient.post(FORECAST_ACCURACY_URL, timeSeriesAnalysisRequest, Double.class);
    } catch (HttpServerErrorException exception) {
      String engineException = format("Failed on post computeForecastAccuracy to engine: %s", exception.getResponseBodyAsString());
      throw new TimeSeriesAnalysisEngineRequestException(engineException, exception);
    } catch (Exception exception) {
      throw new TimeSeriesAnalysisEngineRequestException("Failed to post computeForecastAccuracy to engine", exception);
    }
  }

  @Override
  public TimeSeries predict(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    try {
      return restClient.post(PREDICATE_URL, timeSeriesAnalysisRequest, TimeSeries.class);
    } catch (HttpServerErrorException exception) {
      String engineException = format("Failed on post predict to engine: %s", exception.getResponseBodyAsString());
      throw new TimeSeriesAnalysisEngineRequestException(engineException, exception);
    } catch (Exception exception) {
      throw new TimeSeriesAnalysisEngineRequestException("Failed to post predict to engine", exception);
    }
  }
}