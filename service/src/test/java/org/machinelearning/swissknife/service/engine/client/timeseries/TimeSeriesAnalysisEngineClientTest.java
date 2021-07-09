package org.machinelearning.swissknife.service.engine.client.timeseries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.machinelearning.swissknife.lib.rest.RestClient;
import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesRow;
import org.machinelearning.swissknife.service.engine.client.timeseries.exceptions.TimeSeriesAnalysisEngineRequestException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.security.InvalidParameterException;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.machinelearning.swissknife.lib.endpoints.TimeSeriesAnalysisUrls.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeSeriesAnalysisEngineClientTest {

  @Mock
  private RestClient restClient;
  private TimeSeriesAnalysisEngineClient client;

  @BeforeEach
  public void setup() {
    this.client = new TimeSeriesAnalysisEngineClient(restClient);
  }

  @Test
  public void should_delegate_forecast_call_to_engine() {
    List<TimeSeriesRow> timeSeriesRows = asList(new TimeSeriesRow("1960", 1.), new TimeSeriesRow("1961", 2.));
    TimeSeries timeSeries = new TimeSeries(timeSeriesRows, "Date", "Value", "%Y");
    TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = new TimeSeriesAnalysisRequest(timeSeries, 1);
    TimeSeries responseTimeSeries = new TimeSeries(singletonList(new TimeSeriesRow("1962", 3.)), "Date", "Value", "%Y");
    onPostReturn(responseTimeSeries);

    TimeSeries actual_forecasted = client.forecast(timeSeriesAnalysisRequest);

    verify(restClient).post(FORECAST_URL, timeSeriesAnalysisRequest, TimeSeries.class);
    assertEquals(responseTimeSeries, actual_forecasted);
  }

  @Test
  public void should_rethrow_time_series_exception_on_forecast_failure() {
    doThrowExceptionOnPost(new InvalidParameterException());

    try {
      client.forecast(mock(TimeSeriesAnalysisRequest.class));
      fail("should fail");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisEngineRequestException.class, exception);
      assertEquals("Failed to post forecast to engine", exception.getMessage());
      assertInstanceOf(InvalidParameterException.class, exception.getCause());
    }
  }

  @Test
  public void should_throw_exception_with_body_on_forecast_failure_with_http_server_error_exception() {
    doThrowExceptionOnPost(buildHttpServerErrorException("Original Forecast Exception Message"));

    try {
      client.forecast(mock(TimeSeriesAnalysisRequest.class));
      fail("should fail");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisEngineRequestException.class, exception);
      assertEquals("Failed on post forecast to engine: Original Forecast Exception Message", exception.getMessage());
      assertInstanceOf(HttpServerErrorException.class, exception.getCause());
    }
  }

  @Test
  public void should_delegate_compute_forecast_accuracy_call_to_engine() {
    List<TimeSeriesRow> timeSeriesRows = asList(new TimeSeriesRow("1960", 1.), new TimeSeriesRow("1961", 2.));
    TimeSeries timeSeries = new TimeSeries(timeSeriesRows, "Date", "Value", "%Y");
    TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = new TimeSeriesAnalysisRequest(timeSeries, 1);
    onPostReturn(2.);

    Double actualAccuracy = client.computeForecastAccuracy(timeSeriesAnalysisRequest);

    verify(restClient).post(FORECAST_ACCURACY_URL, timeSeriesAnalysisRequest, Double.class);
    assertEquals(2.d, actualAccuracy);
  }

  @Test
  public void should_rethrow_time_series_exception_on_forecast_accuracy_failure() {
    doThrowExceptionOnPost(new InvalidParameterException());

    try {
      client.computeForecastAccuracy(mock(TimeSeriesAnalysisRequest.class));
      fail("should fail");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisEngineRequestException.class, exception);
      assertEquals("Failed to post computeForecastAccuracy to engine", exception.getMessage());
      assertInstanceOf(InvalidParameterException.class, exception.getCause());
    }
  }

  @Test
  public void should_throw_exception_with_body_on_forecast_accuracy_failure_with_http_server_error_exception() {
    doThrowExceptionOnPost(buildHttpServerErrorException("Original Forecast Accuracy Exception Message"));

    try {
      client.computeForecastAccuracy(mock(TimeSeriesAnalysisRequest.class));
      fail("should fail");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisEngineRequestException.class, exception);
      assertEquals("Failed on post computeForecastAccuracy to engine: Original Forecast Accuracy Exception Message", exception.getMessage());
      assertInstanceOf(HttpServerErrorException.class, exception.getCause());
    }
  }

  @Test
  public void should_delegate_predict_call_to_engine() {
    List<TimeSeriesRow> timeSeriesRows = asList(new TimeSeriesRow("1960", 1.), new TimeSeriesRow("1961", 2.));
    TimeSeries timeSeries = new TimeSeries(timeSeriesRows, "Date", "Value", "%Y");
    TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = new TimeSeriesAnalysisRequest(timeSeries, 1);
    TimeSeries responseTimeSeries = new TimeSeries(singletonList(new TimeSeriesRow("1962", 3.)), "Date", "Value", "%Y");
    onPostReturn(responseTimeSeries);

    TimeSeries actual_predicted = client.predict(timeSeriesAnalysisRequest);

    verify(restClient).post(PREDICATE_URL, timeSeriesAnalysisRequest, TimeSeries.class);
    assertEquals(responseTimeSeries, actual_predicted);
  }

  @Test
  public void should_rethrow_time_series_exception_on_predict_failure() {
    doThrowExceptionOnPost(new InvalidParameterException());

    try {
      client.predict(mock(TimeSeriesAnalysisRequest.class));
      fail("should fail");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisEngineRequestException.class, exception);
      assertEquals("Failed to post predict to engine", exception.getMessage());
      assertInstanceOf(InvalidParameterException.class, exception.getCause());
    }
  }

  @Test
  public void should_throw_exception_with_body_on_predict_failure_with_http_server_error_exception() {
    doThrowExceptionOnPost(buildHttpServerErrorException("Original Predict Exception Message"));

    try {
      client.predict(mock(TimeSeriesAnalysisRequest.class));
      fail("should fail");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisEngineRequestException.class, exception);
      assertEquals("Failed on post predict to engine: Original Predict Exception Message", exception.getMessage());
      assertInstanceOf(HttpServerErrorException.class, exception.getCause());
    }
  }

  private void onPostReturn(Object object) {
    when(restClient.post(any(), any(), any())).thenReturn(object);
  }

  private void doThrowExceptionOnPost(Exception exception) {
    when(restClient.post(any(), any(), any())).thenThrow(exception);
  }

  private static HttpServerErrorException buildHttpServerErrorException(String exceptionMessage) {
    return new HttpServerErrorException("message", HttpStatus.INTERNAL_SERVER_ERROR, "status", null, exceptionMessage.getBytes(), null);
  }
}