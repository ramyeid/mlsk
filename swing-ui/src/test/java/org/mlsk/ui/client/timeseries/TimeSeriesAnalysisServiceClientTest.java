package org.mlsk.ui.client.timeseries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.lib.rest.RestClient;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.model.timeseries.TimeSeriesRow;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mlsk.service.utils.TimeSeriesAnalysisUrls.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeSeriesAnalysisServiceClientTest {

  @Mock
  private RestClient restClient;
  private TimeSeriesAnalysisServiceClient client;

  @BeforeEach
  public void setup() {
    this.client = new TimeSeriesAnalysisServiceClient(restClient);
  }

  @Test
  public void should_delegate_forecast_call_to_service() {
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
    doThrowExceptionOnPost(new RuntimeException());

    try {
      client.forecast(mock(TimeSeriesAnalysisRequest.class));
      fail("should fail");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceRequestException.class, exception);
      assertEquals("Failed to post forecast to service", exception.getMessage());
      assertInstanceOf(RuntimeException.class, exception.getCause());
    }
  }

  @Test
  public void should_throw_exception_with_body_on_forecast_failure_with_http_server_error_exception() {
    doThrowExceptionOnPost(buildHttpServerErrorException("Original Forecast Exception Message"));

    try {
      client.forecast(mock(TimeSeriesAnalysisRequest.class));
      fail("should fail");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceRequestException.class, exception);
      assertEquals("Failed on post forecast to service:\nOriginal Forecast Exception Message", exception.getMessage());
      assertInstanceOf(HttpServerErrorException.class, exception.getCause());
    }
  }

  @Test
  public void should_delegate_forecast_vs_actual_call_to_service() {
    List<TimeSeriesRow> timeSeriesRows = asList(new TimeSeriesRow("1960", 1.), new TimeSeriesRow("1961", 2.));
    TimeSeries timeSeries = new TimeSeries(timeSeriesRows, "Date", "Value", "%Y");
    TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = new TimeSeriesAnalysisRequest(timeSeries, 1);
    TimeSeries responseTimeSeries = new TimeSeries(singletonList(new TimeSeriesRow("1962", 3.)), "Date", "Value", "%Y");
    onPostReturn(responseTimeSeries);

    TimeSeries actual_forecasted = client.forecastVsActual(timeSeriesAnalysisRequest);

    verify(restClient).post(FORECAST_VS_ACTUAL_URL, timeSeriesAnalysisRequest, TimeSeries.class);
    assertEquals(responseTimeSeries, actual_forecasted);
  }

  @Test
  public void should_rethrow_time_series_exception_on_forecast_vs_actual_failure() {
    doThrowExceptionOnPost(new RuntimeException());

    try {
      client.forecastVsActual(mock(TimeSeriesAnalysisRequest.class));
      fail("should fail");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceRequestException.class, exception);
      assertEquals("Failed to post forecast vs actual to service", exception.getMessage());
      assertInstanceOf(RuntimeException.class, exception.getCause());
    }
  }

  @Test
  public void should_throw_exception_with_body_on_forecast_vs_actual_failure_with_http_server_error_exception() {
    doThrowExceptionOnPost(buildHttpServerErrorException("Original Forecast Vs Actual Exception Message"));

    try {
      client.forecastVsActual(mock(TimeSeriesAnalysisRequest.class));
      fail("should fail");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceRequestException.class, exception);
      assertEquals("Failed on post forecast vs actual to service:\nOriginal Forecast Vs Actual Exception Message", exception.getMessage());
      assertInstanceOf(HttpServerErrorException.class, exception.getCause());
    }
  }

  @Test
  public void should_delegate_compute_forecast_accuracy_call_to_service() {
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
    doThrowExceptionOnPost(new RuntimeException());

    try {
      client.computeForecastAccuracy(mock(TimeSeriesAnalysisRequest.class));
      fail("should fail");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceRequestException.class, exception);
      assertEquals("Failed to post compute forecast accuracy to service", exception.getMessage());
      assertInstanceOf(RuntimeException.class, exception.getCause());
    }
  }

  @Test
  public void should_throw_exception_with_body_on_forecast_accuracy_failure_with_http_server_error_exception() {
    doThrowExceptionOnPost(buildHttpServerErrorException("Original Forecast Accuracy Exception Message"));

    try {
      client.computeForecastAccuracy(mock(TimeSeriesAnalysisRequest.class));
      fail("should fail");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceRequestException.class, exception);
      assertEquals("Failed on post compute forecast accuracy to service:\nOriginal Forecast Accuracy Exception Message", exception.getMessage());
      assertInstanceOf(HttpServerErrorException.class, exception.getCause());
    }
  }

  @Test
  public void should_delegate_predict_call_to_service() {
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
    doThrowExceptionOnPost(new RuntimeException());

    try {
      client.predict(mock(TimeSeriesAnalysisRequest.class));
      fail("should fail");

    } catch (TimeSeriesAnalysisServiceRequestException exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceRequestException.class, exception);
      assertEquals("Failed to post predict to service", exception.getMessage());
      assertInstanceOf(RuntimeException.class, exception.getCause());
    }
  }

  @Test
  public void should_throw_exception_with_body_on_predict_failure_with_http_server_error_exception() {
    doThrowExceptionOnPost(buildHttpServerErrorException("Original Predict Exception Message"));

    try {
      client.predict(mock(TimeSeriesAnalysisRequest.class));
      fail("should fail");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceRequestException.class, exception);
      assertEquals("Failed on post predict to service:\nOriginal Predict Exception Message", exception.getMessage());
      assertInstanceOf(HttpServerErrorException.class, exception.getCause());
    }
  }

  private void doThrowExceptionOnPost(Exception exception) {
    when(restClient.post(any(), any(), any())).thenThrow(exception);
  }

  private void onPostReturn(Object object) {
    when(restClient.post(any(), any(), any())).thenReturn(object);
  }

  private static HttpServerErrorException buildHttpServerErrorException(String exceptionMessage) {
    return new HttpServerErrorException("message", HttpStatus.INTERNAL_SERVER_ERROR, "status", null, exceptionMessage.getBytes(), null);
  }
}