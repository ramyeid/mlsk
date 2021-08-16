package org.mlsk.service.impl.timeseries.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.lib.rest.RestClient;
import org.mlsk.service.impl.timeseries.engine.exceptions.TimeSeriesAnalysisEngineRequestException;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.model.timeseries.TimeSeriesRow;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.security.InvalidParameterException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mlsk.service.timeseries.utils.TimeSeriesAnalysisConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TimeSeriesAnalysisEngineClientTest {

  @Mock
  private RestClient restClient;

  private TimeSeriesAnalysisEngineClient client;

  @BeforeEach
  public void setUp() {
    this.client = new TimeSeriesAnalysisEngineClient(restClient);
  }

  @Test
  public void should_delegate_forecast_call_to_engine() {
    List<TimeSeriesRow> timeSeriesRows = newArrayList(new TimeSeriesRow("1960", 1.), new TimeSeriesRow("1961", 2.));
    TimeSeries timeSeries = new TimeSeries(timeSeriesRows, "Date", "Value", "%Y");
    TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = new TimeSeriesAnalysisRequest(timeSeries, 1);
    TimeSeries responseTimeSeries = new TimeSeries(newArrayList(new TimeSeriesRow("1962", 3.)), "Date", "Value", "%Y");
    onPostReturn(responseTimeSeries);

    TimeSeries actualForecasted = client.forecast(timeSeriesAnalysisRequest);

    verifyPostCalled(timeSeriesAnalysisRequest, FORECAST_URL, TimeSeries.class);
    assertEquals(responseTimeSeries, actualForecasted);
  }

  @Test
  public void should_rethrow_time_series_exception_on_forecast_failure() {
    doThrowExceptionOnPost(new InvalidParameterException());

    try {
      client.forecast(mock(TimeSeriesAnalysisRequest.class));
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisEngineRequestException(exception, "Failed to post forecast to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_forecast_failure_with_http_server_error_exception() {
    doThrowExceptionOnPost(buildHttpServerErrorException("Original Forecast Exception Message"));

    try {
      client.forecast(mock(TimeSeriesAnalysisRequest.class));
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisEngineRequestExceptionWithServerError(exception, "Failed on post forecast to engine: Original Forecast Exception Message");
    }
  }

  @Test
  public void should_delegate_compute_forecast_accuracy_call_to_engine() {
    List<TimeSeriesRow> timeSeriesRows = newArrayList(new TimeSeriesRow("1960", 1.), new TimeSeriesRow("1961", 2.));
    TimeSeries timeSeries = new TimeSeries(timeSeriesRows, "Date", "Value", "%Y");
    TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = new TimeSeriesAnalysisRequest(timeSeries, 1);
    onPostReturn(2.);

    Double actualAccuracy = client.computeForecastAccuracy(timeSeriesAnalysisRequest);

    verifyPostCalled(timeSeriesAnalysisRequest, FORECAST_ACCURACY_URL, Double.class);
    assertEquals(2.d, actualAccuracy);
  }

  @Test
  public void should_rethrow_time_series_exception_on_forecast_accuracy_failure() {
    doThrowExceptionOnPost(new InvalidParameterException());

    try {
      client.computeForecastAccuracy(mock(TimeSeriesAnalysisRequest.class));
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisEngineRequestException(exception, "Failed to post computeForecastAccuracy to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_forecast_accuracy_failure_with_http_server_error_exception() {
    doThrowExceptionOnPost(buildHttpServerErrorException("Original Forecast Accuracy Exception Message"));

    try {
      client.computeForecastAccuracy(mock(TimeSeriesAnalysisRequest.class));
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisEngineRequestExceptionWithServerError(exception, "Failed on post computeForecastAccuracy to engine: Original Forecast Accuracy Exception Message");
    }
  }

  @Test
  public void should_delegate_predict_call_to_engine() {
    List<TimeSeriesRow> timeSeriesRows = newArrayList(new TimeSeriesRow("1960", 1.), new TimeSeriesRow("1961", 2.));
    TimeSeries timeSeries = new TimeSeries(timeSeriesRows, "Date", "Value", "%Y");
    TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = new TimeSeriesAnalysisRequest(timeSeries, 1);
    TimeSeries responseTimeSeries = new TimeSeries(newArrayList(new TimeSeriesRow("1962", 3.)), "Date", "Value", "%Y");
    onPostReturn(responseTimeSeries);

    TimeSeries actualPredicted = client.predict(timeSeriesAnalysisRequest);

    verifyPostCalled(timeSeriesAnalysisRequest, PREDICATE_URL, TimeSeries.class);
    assertEquals(responseTimeSeries, actualPredicted);
  }

  @Test
  public void should_rethrow_time_series_exception_on_predict_failure() {
    doThrowExceptionOnPost(new InvalidParameterException());

    try {
      client.predict(mock(TimeSeriesAnalysisRequest.class));
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisEngineRequestException(exception, "Failed to post predict to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_predict_failure_with_http_server_error_exception() {
    doThrowExceptionOnPost(buildHttpServerErrorException("Original Predict Exception Message"));

    try {
      client.predict(mock(TimeSeriesAnalysisRequest.class));
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisEngineRequestExceptionWithServerError(exception, "Failed on post predict to engine: Original Predict Exception Message");
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

  private static void assertOnTimeSeriesAnalysisEngineRequestException(Exception exception, String exceptionMessage) {
    assertInstanceOf(TimeSeriesAnalysisEngineRequestException.class, exception);
    assertEquals(exceptionMessage, exception.getMessage());
    assertInstanceOf(InvalidParameterException.class, exception.getCause());
  }

  private static void assertOnTimeSeriesAnalysisEngineRequestExceptionWithServerError(Exception exception, String exceptionMessage) {
    assertInstanceOf(TimeSeriesAnalysisEngineRequestException.class, exception);
    assertEquals(exceptionMessage, exception.getMessage());
    assertInstanceOf(HttpServerErrorException.class, exception.getCause());
  }

  private <T> void verifyPostCalled(TimeSeriesAnalysisRequest request, String resource, Class<T> bodyType) {
    InOrder inOrder = inOrder(restClient);
    inOrder.verify(restClient).post(resource, request, bodyType);
    inOrder.verifyNoMoreInteractions();
  }
}