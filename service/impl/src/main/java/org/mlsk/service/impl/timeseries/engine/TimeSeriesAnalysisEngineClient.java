package org.mlsk.service.impl.timeseries.engine;

import com.google.common.annotations.VisibleForTesting;
import org.mlsk.api.engine.timeseries.client.TimeSeriesAnalysisEngineApi;
import org.mlsk.api.engine.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.lib.model.Endpoint;
import org.mlsk.service.impl.engine.client.EngineClientFactory;
import org.mlsk.service.impl.timeseries.engine.exception.TimeSeriesAnalysisEngineRequestException;
import org.mlsk.service.impl.timeseries.engine.mapper.TimeSeriesAnalysisRequestMapper;
import org.mlsk.service.impl.timeseries.engine.mapper.TimeSeriesMapper;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.timeseries.TimeSeriesAnalysisEngine;
import org.springframework.web.client.HttpServerErrorException;

import static java.lang.String.format;

public class TimeSeriesAnalysisEngineClient implements TimeSeriesAnalysisEngine {

  private final TimeSeriesAnalysisEngineApi timeSeriesAnalysisClient;

  public TimeSeriesAnalysisEngineClient(Endpoint endpoint, EngineClientFactory engineClientFactory) {
    this(engineClientFactory.buildTimeSeriesAnalysisClient(endpoint));
  }

  @VisibleForTesting
  TimeSeriesAnalysisEngineClient(TimeSeriesAnalysisEngineApi timeSeriesAnalysisClient) {
    this.timeSeriesAnalysisClient = timeSeriesAnalysisClient;
  }

  @Override
  public TimeSeries forecast(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    try {
      TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequestModel = TimeSeriesAnalysisRequestMapper.toEngineModel(timeSeriesAnalysisRequest);
      return TimeSeriesMapper.fromEngineModel(timeSeriesAnalysisClient.forecast(timeSeriesAnalysisRequestModel));
    } catch (HttpServerErrorException exception) {
      throw buildTimeSeriesAnalysisEngineRequestException(exception, "forecast");
    } catch (Exception exception) {
      throw buildTimeSeriesAnalysisEngineRequestException(exception, "forecast");
    }
  }

  @Override
  public Double computeForecastAccuracy(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    try {
      TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequestModel = TimeSeriesAnalysisRequestMapper.toEngineModel(timeSeriesAnalysisRequest);
      return timeSeriesAnalysisClient.computeAccuracyOfForecast(timeSeriesAnalysisRequestModel).doubleValue();
    } catch (HttpServerErrorException exception) {
      throw buildTimeSeriesAnalysisEngineRequestException(exception, "computeForecastAccuracy");
    } catch (Exception exception) {
      throw buildTimeSeriesAnalysisEngineRequestException(exception, "computeForecastAccuracy");
    }
  }

  @Override
  public TimeSeries predict(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    try {
      TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequestModel = TimeSeriesAnalysisRequestMapper.toEngineModel(timeSeriesAnalysisRequest);
      return TimeSeriesMapper.fromEngineModel(timeSeriesAnalysisClient.predict(timeSeriesAnalysisRequestModel));
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
