package org.machinelearning.swissknife.ui.client.timeseries;

import com.google.common.annotations.VisibleForTesting;
import org.machinelearning.swissknife.TimeSeriesAnalysis;
import org.machinelearning.swissknife.lib.rest.RestClient;
import org.machinelearning.swissknife.model.ServiceInformation;
import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.springframework.web.client.HttpServerErrorException;

import static java.lang.String.format;
import static org.machinelearning.swissknife.lib.endpoints.TimeSeriesAnalysisUrls.*;

public class TimeSeriesAnalysisServiceClient implements TimeSeriesAnalysis {

  private final RestClient restClient;

  public TimeSeriesAnalysisServiceClient(ServiceInformation serviceInformation) {
    this.restClient = new RestClient(serviceInformation);
  }

  @VisibleForTesting
  public TimeSeriesAnalysisServiceClient(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public TimeSeries forecast(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    try {
      return restClient.post(FORECAST_URL, timeSeriesAnalysisRequest, TimeSeries.class);
    } catch (HttpServerErrorException exception) {
      String serviceException = format("Failed on post forecast to service:\n%s", exception.getResponseBodyAsString());
      throw new TimeSeriesAnalysisServiceRequestException(serviceException, exception);
    } catch (Exception exception) {
      throw new TimeSeriesAnalysisServiceRequestException("Failed to post forecast to service", exception);
    }
  }

  @Override
  public TimeSeries forecastVsActual(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    try {
      return restClient.post(FORECAST_VS_ACTUAL_URL, timeSeriesAnalysisRequest, TimeSeries.class);
    } catch (HttpServerErrorException exception) {
      String serviceException = format("Failed on post forecast vs actual to service:\n%s", exception.getResponseBodyAsString());
      throw new TimeSeriesAnalysisServiceRequestException(serviceException, exception);
    } catch (Exception exception) {
      throw new TimeSeriesAnalysisServiceRequestException("Failed to post forecast vs actual to service", exception);
    }
  }

  @Override
  public Double computeForecastAccuracy(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    try {
      return restClient.post(FORECAST_ACCURACY_URL, timeSeriesAnalysisRequest, Double.class);
    } catch (HttpServerErrorException exception) {
      String serviceException = format("Failed on post compute forecast accuracy to service:\n%s", exception.getResponseBodyAsString());
      throw new TimeSeriesAnalysisServiceRequestException(serviceException, exception);
    } catch (Exception exception) {
      throw new TimeSeriesAnalysisServiceRequestException("Failed to post compute forecast accuracy to service", exception);
    }
  }

  @Override
  public TimeSeries predict(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    try {
      return restClient.post(PREDICATE_URL, timeSeriesAnalysisRequest, TimeSeries.class);
    } catch (HttpServerErrorException exception) {
      String serviceException = format("Failed on post predict to service:\n%s", exception.getResponseBodyAsString());
      throw new TimeSeriesAnalysisServiceRequestException(serviceException, exception);
    } catch (Exception exception) {
      throw new TimeSeriesAnalysisServiceRequestException("Failed to post predict to service", exception);
    }
  }
}
