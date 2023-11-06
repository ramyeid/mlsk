package org.mlsk.ui.timeseries.service.client;

import com.google.common.annotations.VisibleForTesting;
import org.mlsk.api.service.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.service.timeseries.model.TimeSeriesModel;
import org.mlsk.lib.model.Endpoint;
import org.mlsk.lib.rest.RestClient;
import org.mlsk.ui.timeseries.service.client.exception.TimeSeriesAnalysisServiceRequestException;
import org.springframework.web.client.HttpServerErrorException;

import java.math.BigDecimal;

import static java.lang.String.format;
import static org.mlsk.service.timeseries.utils.TimeSeriesAnalysisConstants.*;

public class TimeSeriesAnalysisServiceClient {

  private final RestClient restClient;

  public TimeSeriesAnalysisServiceClient(Endpoint endpoint) {
    this(new RestClient(endpoint));
  }

  @VisibleForTesting
  public TimeSeriesAnalysisServiceClient(RestClient restClient) {
    this.restClient = restClient;
  }

  public TimeSeriesModel forecast(TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequest) {
    try {
      return restClient.post(FORECAST_URL, timeSeriesAnalysisRequest, TimeSeriesModel.class);
    } catch (HttpServerErrorException exception) {
      String serviceException = format("Failed on post forecast to service:%n%s", exception.getResponseBodyAsString());
      throw new TimeSeriesAnalysisServiceRequestException(serviceException, exception);
    } catch (Exception exception) {
      throw new TimeSeriesAnalysisServiceRequestException("Failed to post forecast to service", exception);
    }
  }

  public TimeSeriesModel forecastVsActual(TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequest) {
    try {
      return restClient.post(FORECAST_VS_ACTUAL_URL, timeSeriesAnalysisRequest, TimeSeriesModel.class);
    } catch (HttpServerErrorException exception) {
      String serviceException = format("Failed on post forecast vs actual to service:%n%s", exception.getResponseBodyAsString());
      throw new TimeSeriesAnalysisServiceRequestException(serviceException, exception);
    } catch (Exception exception) {
      throw new TimeSeriesAnalysisServiceRequestException("Failed to post forecast vs actual to service", exception);
    }
  }

  public BigDecimal computeForecastAccuracy(TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequest) {
    try {
      return restClient.post(FORECAST_ACCURACY_URL, timeSeriesAnalysisRequest, BigDecimal.class);
    } catch (HttpServerErrorException exception) {
      String serviceException = format("Failed on post compute forecast accuracy to service:%n%s", exception.getResponseBodyAsString());
      throw new TimeSeriesAnalysisServiceRequestException(serviceException, exception);
    } catch (Exception exception) {
      throw new TimeSeriesAnalysisServiceRequestException("Failed to post compute forecast accuracy to service", exception);
    }
  }

  public TimeSeriesModel predict(TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequest) {
    try {
      return restClient.post(PREDICT_URL, timeSeriesAnalysisRequest, TimeSeriesModel.class);
    } catch (HttpServerErrorException exception) {
      String serviceException = format("Failed on post predict to service:%n%s", exception.getResponseBodyAsString());
      throw new TimeSeriesAnalysisServiceRequestException(serviceException, exception);
    } catch (Exception exception) {
      throw new TimeSeriesAnalysisServiceRequestException("Failed to post predict to service", exception);
    }
  }
}
