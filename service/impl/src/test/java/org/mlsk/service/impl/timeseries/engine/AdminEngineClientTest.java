package org.mlsk.service.impl.timeseries.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.engine.timeseries.client.TimeSeriesAnalysisEngineApi;
import org.mlsk.api.engine.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.engine.timeseries.model.TimeSeriesModel;
import org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel;
import org.mlsk.service.impl.timeseries.engine.exception.TimeSeriesAnalysisEngineRequestException;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.model.timeseries.TimeSeriesRow;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.security.InvalidParameterException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminEngineClientTest {

  @Mock
  private TimeSeriesAnalysisEngineApi timeSeriesAnalysisEngineApi;

  private TimeSeriesAnalysisEngineClient client;

  @BeforeEach
  public void setUp() {
    this.client = new TimeSeriesAnalysisEngineClient(timeSeriesAnalysisEngineApi);
  }

  @Test
  public void should_delegate_forecast_call_to_engine() {
    TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = buildTimeSeriesAnalysisRequest();
    when(timeSeriesAnalysisEngineApi.forecast(buildTimeSeriesAnalysisRequestModel())).thenReturn(buildTimeSeriesModelResponse());

    TimeSeries actualForecasted = client.forecast(timeSeriesAnalysisRequest);

    verify(timeSeriesAnalysisEngineApi).forecast(buildTimeSeriesAnalysisRequestModel());
    assertEquals(buildTimeSeriesResponse(), actualForecasted);
  }

  @Test
  public void should_rethrow_time_series_exception_on_forecast_failure() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    doThrow(new InvalidParameterException()).when(timeSeriesAnalysisEngineApi).forecast(any());

    try {
      client.forecast(request);
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisEngineRequestException(exception, "Failed to post forecast to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_forecast_failure_with_http_server_error_exception() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    doThrow(buildHttpServerErrorException("Original Forecast Exception Message")).when(timeSeriesAnalysisEngineApi).forecast(any());

    try {
      client.forecast(request);
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisEngineRequestExceptionWithServerError(exception, "Failed on post forecast to engine: Original Forecast Exception Message");
    }
  }

  @Test
  public void should_delegate_compute_forecast_accuracy_call_to_engine() {
    TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = buildTimeSeriesAnalysisRequest();
    when(timeSeriesAnalysisEngineApi.computeAccuracyOfForecast(buildTimeSeriesAnalysisRequestModel())).thenReturn(valueOf(2.));

    Double actualAccuracy = client.computeForecastAccuracy(timeSeriesAnalysisRequest);

    verify(timeSeriesAnalysisEngineApi).computeAccuracyOfForecast(buildTimeSeriesAnalysisRequestModel());
    assertEquals(2.d, actualAccuracy);
  }

  @Test
  public void should_rethrow_time_series_exception_on_forecast_accuracy_failure() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    doThrow(new InvalidParameterException()).when(timeSeriesAnalysisEngineApi).computeAccuracyOfForecast(any());

    try {
      client.computeForecastAccuracy(request);
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisEngineRequestException(exception, "Failed to post computeForecastAccuracy to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_forecast_accuracy_failure_with_http_server_error_exception() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    doThrow(buildHttpServerErrorException("Original Forecast Accuracy Exception Message")).when(timeSeriesAnalysisEngineApi).computeAccuracyOfForecast(any());

    try {
      client.computeForecastAccuracy(request);
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisEngineRequestExceptionWithServerError(exception, "Failed on post computeForecastAccuracy to engine: Original Forecast Accuracy Exception Message");
    }
  }

  @Test
  public void should_delegate_predict_call_to_engine() {
    TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = buildTimeSeriesAnalysisRequest();
    when(timeSeriesAnalysisEngineApi.predict(buildTimeSeriesAnalysisRequestModel())).thenReturn(buildTimeSeriesModelResponse());

    TimeSeries actualPredicted = client.predict(timeSeriesAnalysisRequest);

    verify(timeSeriesAnalysisEngineApi).predict(buildTimeSeriesAnalysisRequestModel());
    assertEquals(buildTimeSeriesResponse(), actualPredicted);
  }

  @Test
  public void should_rethrow_time_series_exception_on_predict_failure() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    doThrow(new InvalidParameterException()).when(timeSeriesAnalysisEngineApi).predict(any());

    try {
      client.predict(request);
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisEngineRequestException(exception, "Failed to post predict to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_predict_failure_with_http_server_error_exception() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    doThrow(buildHttpServerErrorException("Original Predict Exception Message")).when(timeSeriesAnalysisEngineApi).predict(any());

    try {
      client.predict(request);
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisEngineRequestExceptionWithServerError(exception, "Failed on post predict to engine: Original Predict Exception Message");
    }
  }

  private static TimeSeriesAnalysisRequest buildTimeSeriesAnalysisRequest() {
    List<TimeSeriesRow> timeSeriesRows = newArrayList(new TimeSeriesRow("1960", 1.), new TimeSeriesRow("1961", 2.));
    TimeSeries timeSeries = new TimeSeries(timeSeriesRows, "Date", "Value", "%Y");
    return new TimeSeriesAnalysisRequest(1L, timeSeries, 1);
  }

  private static TimeSeriesAnalysisRequestModel buildTimeSeriesAnalysisRequestModel() {
    List<TimeSeriesRowModel> timeSeriesRows = newArrayList(new TimeSeriesRowModel("1960", valueOf(1.)), new TimeSeriesRowModel("1961", valueOf(2.)));
    TimeSeriesModel timeSeries = new TimeSeriesModel(timeSeriesRows, "Date", "Value", "%Y");
    return new TimeSeriesAnalysisRequestModel(1L, timeSeries, 1);
  }

  private static TimeSeries buildTimeSeriesResponse() {
    return new TimeSeries(newArrayList(new TimeSeriesRow("1962", 3.)), "Date", "Value", "%Y");
  }

  private static TimeSeriesModel buildTimeSeriesModelResponse() {
    return new TimeSeriesModel(newArrayList(new TimeSeriesRowModel("1962", valueOf(3.))), "Date", "Value", "%Y");
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
}