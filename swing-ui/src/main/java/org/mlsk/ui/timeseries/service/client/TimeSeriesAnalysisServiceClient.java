package org.mlsk.ui.timeseries.service.client;

import com.google.common.annotations.VisibleForTesting;
import org.mlsk.api.service.timeseries.client.ApiClient;
import org.mlsk.api.service.timeseries.client.TimeSeriesAnalysisServiceApi;
import org.mlsk.api.service.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.service.timeseries.model.TimeSeriesModel;
import org.mlsk.lib.model.Endpoint;
import org.mlsk.ui.timeseries.service.client.exception.TimeSeriesAnalysisServiceRequestException;
import org.springframework.web.client.HttpServerErrorException;

import java.math.BigDecimal;

import static java.lang.String.format;
import static org.mlsk.lib.rest.RestTemplateFactory.buildRestTemplate;

public class TimeSeriesAnalysisServiceClient {

  private final TimeSeriesAnalysisServiceApi timeSeriesAnalysisServiceApi;

  public TimeSeriesAnalysisServiceClient(Endpoint endpoint) {
    this(new TimeSeriesAnalysisServiceApi(new ApiClient(buildRestTemplate()).setBasePath(endpoint.getUrl())));
  }

  @VisibleForTesting
  public TimeSeriesAnalysisServiceClient(TimeSeriesAnalysisServiceApi timeSeriesAnalysisServiceApi) {
    this.timeSeriesAnalysisServiceApi = timeSeriesAnalysisServiceApi;
  }

  public TimeSeriesModel forecast(TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequest) {
    try {
      return this.timeSeriesAnalysisServiceApi.forecast(timeSeriesAnalysisRequest);
    } catch (HttpServerErrorException exception) {
      String serviceException = format("Failed on post forecast to service:%n%s", exception.getResponseBodyAsString());
      throw new TimeSeriesAnalysisServiceRequestException(serviceException, exception);
    } catch (Exception exception) {
      throw new TimeSeriesAnalysisServiceRequestException("Failed to post forecast to service", exception);
    }
  }

  public TimeSeriesModel forecastVsActual(TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequest) {
    try {
      return this.timeSeriesAnalysisServiceApi.forecastVsActual(timeSeriesAnalysisRequest);
    } catch (HttpServerErrorException exception) {
      String serviceException = format("Failed on post forecast vs actual to service:%n%s", exception.getResponseBodyAsString());
      throw new TimeSeriesAnalysisServiceRequestException(serviceException, exception);
    } catch (Exception exception) {
      throw new TimeSeriesAnalysisServiceRequestException("Failed to post forecast vs actual to service", exception);
    }
  }

  public BigDecimal computeForecastAccuracy(TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequest) {
    try {
      return this.timeSeriesAnalysisServiceApi.computeForecastAccuracy(timeSeriesAnalysisRequest);
    } catch (HttpServerErrorException exception) {
      String serviceException = format("Failed on post compute forecast accuracy to service:%n%s", exception.getResponseBodyAsString());
      throw new TimeSeriesAnalysisServiceRequestException(serviceException, exception);
    } catch (Exception exception) {
      throw new TimeSeriesAnalysisServiceRequestException("Failed to post compute forecast accuracy to service", exception);
    }
  }

  public TimeSeriesModel predict(TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequest) {
    try {
      return this.timeSeriesAnalysisServiceApi.predict(timeSeriesAnalysisRequest);
    } catch (HttpServerErrorException exception) {
      String serviceException = format("Failed on post predict to service:%n%s", exception.getResponseBodyAsString());
      throw new TimeSeriesAnalysisServiceRequestException(serviceException, exception);
    } catch (Exception exception) {
      throw new TimeSeriesAnalysisServiceRequestException("Failed to post predict to service", exception);
    }
  }
}
