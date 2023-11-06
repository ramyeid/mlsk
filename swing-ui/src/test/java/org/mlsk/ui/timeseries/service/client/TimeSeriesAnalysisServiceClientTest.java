package org.mlsk.ui.timeseries.service.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.service.timeseries.client.TimeSeriesAnalysisServiceApi;
import org.mlsk.api.service.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.service.timeseries.model.TimeSeriesModel;
import org.mlsk.api.service.timeseries.model.TimeSeriesRowModel;
import org.mlsk.ui.timeseries.service.client.exception.TimeSeriesAnalysisServiceRequestException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TimeSeriesAnalysisServiceClientTest {

  @Mock
  private TimeSeriesAnalysisServiceApi timeSeriesAnalysisServiceApi;
  private TimeSeriesAnalysisServiceClient client;

  @BeforeEach
  public void setUp() {
    this.client = new TimeSeriesAnalysisServiceClient(timeSeriesAnalysisServiceApi);
  }

  @Test
  public void should_delegate_forecast_call_to_service() {
    TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequest = buildTimeSeriesAnalysisRequestModel();
    when(timeSeriesAnalysisServiceApi.forecast(buildTimeSeriesAnalysisRequestModel())).thenReturn(buildTimeSeriesModelResponse());

    TimeSeriesModel actualForecasted = client.forecast(timeSeriesAnalysisRequest);

    verify(timeSeriesAnalysisServiceApi).forecast(timeSeriesAnalysisRequest);
    assertEquals(buildTimeSeriesModelResponse(), actualForecasted);
  }

  @Test
  public void should_rethrow_time_series_exception_on_forecast_failure() {
    doThrow(new RuntimeException()).when(timeSeriesAnalysisServiceApi).forecast(any());

    try {
      client.forecast(mock(TimeSeriesAnalysisRequestModel.class));
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceRequestException(exception, "Failed to post forecast to service");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_forecast_failure_with_http_server_error_exception() {
    doThrow(buildHttpServerErrorException("Original Forecast Exception Message")).when(timeSeriesAnalysisServiceApi).forecast(any());

    try {
      client.forecast(mock(TimeSeriesAnalysisRequestModel.class));
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceRequestExceptionWithServerError(exception, "Failed on post forecast to service:\nOriginal Forecast Exception Message");
    }
  }

  @Test
  public void should_delegate_forecast_vs_actual_call_to_service() {
    TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequest = buildTimeSeriesAnalysisRequestModel();
    when(timeSeriesAnalysisServiceApi.forecastVsActual(buildTimeSeriesAnalysisRequestModel())).thenReturn(buildTimeSeriesModelResponse());

    TimeSeriesModel actualForecasted = client.forecastVsActual(timeSeriesAnalysisRequest);

    verify(timeSeriesAnalysisServiceApi).forecastVsActual(buildTimeSeriesAnalysisRequestModel());
    assertEquals(buildTimeSeriesModelResponse(), actualForecasted);
  }

  @Test
  public void should_rethrow_time_series_exception_on_forecast_vs_actual_failure() {
    doThrow(new RuntimeException()).when(timeSeriesAnalysisServiceApi).forecastVsActual(any());

    try {
      client.forecastVsActual(mock(TimeSeriesAnalysisRequestModel.class));
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceRequestException(exception, "Failed to post forecast vs actual to service");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_forecast_vs_actual_failure_with_http_server_error_exception() {
    doThrow(buildHttpServerErrorException("Original Forecast Vs Actual Exception Message")).when(timeSeriesAnalysisServiceApi).forecastVsActual(any());

    try {
      client.forecastVsActual(mock(TimeSeriesAnalysisRequestModel.class));
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceRequestExceptionWithServerError(exception, "Failed on post forecast vs actual to service:\nOriginal Forecast Vs Actual Exception Message");
    }
  }

  @Test
  public void should_delegate_compute_forecast_accuracy_call_to_service() {
    TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequest = buildTimeSeriesAnalysisRequestModel();
    when(timeSeriesAnalysisServiceApi.computeForecastAccuracy(buildTimeSeriesAnalysisRequestModel())).thenReturn(BigDecimal.valueOf(2.));

    BigDecimal actualAccuracy = client.computeForecastAccuracy(timeSeriesAnalysisRequest);

    verify(timeSeriesAnalysisServiceApi).computeForecastAccuracy(buildTimeSeriesAnalysisRequestModel());
    assertEquals(2.d, actualAccuracy.doubleValue());
  }

  @Test
  public void should_rethrow_time_series_exception_on_forecast_accuracy_failure() {
    doThrow(new RuntimeException()).when(timeSeriesAnalysisServiceApi).computeForecastAccuracy(any());

    try {
      client.computeForecastAccuracy(mock(TimeSeriesAnalysisRequestModel.class));
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceRequestException(exception, "Failed to post compute forecast accuracy to service");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_forecast_accuracy_failure_with_http_server_error_exception() {
    doThrow(buildHttpServerErrorException("Original Forecast Accuracy Exception Message")).when(timeSeriesAnalysisServiceApi).computeForecastAccuracy(any());

    try {
      client.computeForecastAccuracy(mock(TimeSeriesAnalysisRequestModel.class));
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceRequestExceptionWithServerError(exception, "Failed on post compute forecast accuracy to service:\nOriginal Forecast Accuracy Exception Message");
    }
  }

  @Test
  public void should_delegate_predict_call_to_service() {
    TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequest = buildTimeSeriesAnalysisRequestModel();
    when(timeSeriesAnalysisServiceApi.predict(buildTimeSeriesAnalysisRequestModel())).thenReturn(buildTimeSeriesModelResponse());

    TimeSeriesModel actualPredicted = client.predict(timeSeriesAnalysisRequest);

    verify(timeSeriesAnalysisServiceApi).predict(buildTimeSeriesAnalysisRequestModel());
    assertEquals(buildTimeSeriesModelResponse(), actualPredicted);
  }

  @Test
  public void should_rethrow_time_series_exception_on_predict_failure() {
    doThrow(new RuntimeException()).when(timeSeriesAnalysisServiceApi).predict(any());

    try {
      client.predict(mock(TimeSeriesAnalysisRequestModel.class));
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceRequestException(exception, "Failed to post predict to service");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_predict_failure_with_http_server_error_exception() {
    doThrow(buildHttpServerErrorException("Original Predict Exception Message")).when(timeSeriesAnalysisServiceApi).predict(any());

    try {
      client.predict(mock(TimeSeriesAnalysisRequestModel.class));
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceRequestExceptionWithServerError(exception, "Failed on post predict to service:\nOriginal Predict Exception Message");
    }
  }

  private static TimeSeriesAnalysisRequestModel buildTimeSeriesAnalysisRequestModel() {
    List<TimeSeriesRowModel> timeSeriesRows = newArrayList(new TimeSeriesRowModel("1960", valueOf(1.)), new TimeSeriesRowModel("1961", valueOf(2.)));
    TimeSeriesModel timeSeries = new TimeSeriesModel(timeSeriesRows, "Date", "Value", "%Y");
    return new TimeSeriesAnalysisRequestModel(timeSeries, 1);
  }

  private static TimeSeriesModel buildTimeSeriesModelResponse() {
    return new TimeSeriesModel(newArrayList(new TimeSeriesRowModel("1962", valueOf(3.))), "Date", "Value", "%Y");
  }

  private static HttpServerErrorException buildHttpServerErrorException(String exceptionMessage) {
    return new HttpServerErrorException("message", HttpStatus.INTERNAL_SERVER_ERROR, "status", null, exceptionMessage.getBytes(), null);
  }

  private static void assertOnTimeSeriesAnalysisServiceRequestException(Exception exception, String exceptionMessage) {
    assertInstanceOf(TimeSeriesAnalysisServiceRequestException.class, exception);
    assertEquals(exceptionMessage, exception.getMessage());
    assertInstanceOf(RuntimeException.class, exception.getCause());
  }

  private static void assertOnTimeSeriesAnalysisServiceRequestExceptionWithServerError(Exception exception, String exceptionMessage) {
    assertInstanceOf(TimeSeriesAnalysisServiceRequestException.class, exception);
    assertEquals(exceptionMessage, exception.getMessage());
    assertInstanceOf(HttpServerErrorException.class, exception.getCause());
  }
}