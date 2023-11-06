package org.mlsk.ui.timeseries.service.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.service.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.service.timeseries.model.TimeSeriesModel;
import org.mlsk.api.service.timeseries.model.TimeSeriesRowModel;
import org.mlsk.lib.rest.RestClient;
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
import static org.mlsk.service.timeseries.utils.TimeSeriesAnalysisConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TimeSeriesAnalysisServiceClientTest {

  @Mock
  private RestClient restClient;
  private TimeSeriesAnalysisServiceClient client;

  @BeforeEach
  public void setUp() {
    this.client = new TimeSeriesAnalysisServiceClient(restClient);
  }

  @Test
  public void should_delegate_forecast_call_to_service() {
    List<TimeSeriesRowModel> timeSeriesRows = newArrayList(new TimeSeriesRowModel("1960", valueOf(1.)), new TimeSeriesRowModel("1961", valueOf(2.)));
    TimeSeriesModel timeSeries = new TimeSeriesModel(timeSeriesRows, "Date", "Value", "%Y");
    TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequest = new TimeSeriesAnalysisRequestModel(timeSeries, 1);
    TimeSeriesModel responseTimeSeries = new TimeSeriesModel(newArrayList(new TimeSeriesRowModel("1962", valueOf(3.))), "Date", "Value", "%Y");
    onPostReturn(responseTimeSeries);

    TimeSeriesModel actual_forecasted = client.forecast(timeSeriesAnalysisRequest);

    verify(restClient).post(FORECAST_URL, timeSeriesAnalysisRequest, TimeSeriesModel.class);
    assertEquals(responseTimeSeries, actual_forecasted);
  }

  @Test
  public void should_rethrow_time_series_exception_on_forecast_failure() {
    doThrowExceptionOnPost(new RuntimeException());

    try {
      client.forecast(mock(TimeSeriesAnalysisRequestModel.class));
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceRequestException(exception, "Failed to post forecast to service");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_forecast_failure_with_http_server_error_exception() {
    doThrowExceptionOnPost(buildHttpServerErrorException("Original Forecast Exception Message"));

    try {
      client.forecast(mock(TimeSeriesAnalysisRequestModel.class));
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceRequestExceptionWithServerError(exception, "Failed on post forecast to service:\nOriginal Forecast Exception Message");
    }
  }

  @Test
  public void should_delegate_forecast_vs_actual_call_to_service() {
    List<TimeSeriesRowModel> timeSeriesRows = newArrayList(new TimeSeriesRowModel("1960", valueOf(1.)), new TimeSeriesRowModel("1961", valueOf(2.)));
    TimeSeriesModel timeSeries = new TimeSeriesModel(timeSeriesRows, "Date", "Value", "%Y");
    TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequest = new TimeSeriesAnalysisRequestModel(timeSeries, 1);
    TimeSeriesModel responseTimeSeries = new TimeSeriesModel(newArrayList(new TimeSeriesRowModel("1962", valueOf(3.))), "Date", "Value", "%Y");
    onPostReturn(responseTimeSeries);

    TimeSeriesModel actual_forecasted = client.forecastVsActual(timeSeriesAnalysisRequest);

    verify(restClient).post(FORECAST_VS_ACTUAL_URL, timeSeriesAnalysisRequest, TimeSeriesModel.class);
    assertEquals(responseTimeSeries, actual_forecasted);
  }

  @Test
  public void should_rethrow_time_series_exception_on_forecast_vs_actual_failure() {
    doThrowExceptionOnPost(new RuntimeException());

    try {
      client.forecastVsActual(mock(TimeSeriesAnalysisRequestModel.class));
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceRequestException(exception, "Failed to post forecast vs actual to service");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_forecast_vs_actual_failure_with_http_server_error_exception() {
    doThrowExceptionOnPost(buildHttpServerErrorException("Original Forecast Vs Actual Exception Message"));

    try {
      client.forecastVsActual(mock(TimeSeriesAnalysisRequestModel.class));
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceRequestExceptionWithServerError(exception, "Failed on post forecast vs actual to service:\nOriginal Forecast Vs Actual Exception Message");
    }
  }

  @Test
  public void should_delegate_compute_forecast_accuracy_call_to_service() {
    List<TimeSeriesRowModel> timeSeriesRows = newArrayList(new TimeSeriesRowModel("1960", valueOf(1.)), new TimeSeriesRowModel("1961", valueOf(2.)));
    TimeSeriesModel timeSeries = new TimeSeriesModel(timeSeriesRows, "Date", "Value", "%Y");
    TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequest = new TimeSeriesAnalysisRequestModel(timeSeries, 1);
    onPostReturn(BigDecimal.valueOf(2.));

    BigDecimal actualAccuracy = client.computeForecastAccuracy(timeSeriesAnalysisRequest);

    verify(restClient).post(FORECAST_ACCURACY_URL, timeSeriesAnalysisRequest, BigDecimal.class);
    assertEquals(2.d, actualAccuracy.doubleValue());
  }

  @Test
  public void should_rethrow_time_series_exception_on_forecast_accuracy_failure() {
    doThrowExceptionOnPost(new RuntimeException());

    try {
      client.computeForecastAccuracy(mock(TimeSeriesAnalysisRequestModel.class));
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceRequestException(exception, "Failed to post compute forecast accuracy to service");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_forecast_accuracy_failure_with_http_server_error_exception() {
    doThrowExceptionOnPost(buildHttpServerErrorException("Original Forecast Accuracy Exception Message"));

    try {
      client.computeForecastAccuracy(mock(TimeSeriesAnalysisRequestModel.class));
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceRequestExceptionWithServerError(exception, "Failed on post compute forecast accuracy to service:\nOriginal Forecast Accuracy Exception Message");
    }
  }

  @Test
  public void should_delegate_predict_call_to_service() {
    List<TimeSeriesRowModel> timeSeriesRows = newArrayList(new TimeSeriesRowModel("1960", valueOf(1.)), new TimeSeriesRowModel("1961", valueOf(2.)));
    TimeSeriesModel timeSeries = new TimeSeriesModel(timeSeriesRows, "Date", "Value", "%Y");
    TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequest = new TimeSeriesAnalysisRequestModel(timeSeries, 1);
    TimeSeriesModel responseTimeSeries = new TimeSeriesModel(newArrayList(new TimeSeriesRowModel("1962", valueOf(3.))), "Date", "Value", "%Y");
    onPostReturn(responseTimeSeries);

    TimeSeriesModel actual_predicted = client.predict(timeSeriesAnalysisRequest);

    verify(restClient).post(PREDICT_URL, timeSeriesAnalysisRequest, TimeSeriesModel.class);
    assertEquals(responseTimeSeries, actual_predicted);
  }

  @Test
  public void should_rethrow_time_series_exception_on_predict_failure() {
    doThrowExceptionOnPost(new RuntimeException());

    try {
      client.predict(mock(TimeSeriesAnalysisRequestModel.class));
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceRequestException(exception, "Failed to post predict to service");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_predict_failure_with_http_server_error_exception() {
    doThrowExceptionOnPost(buildHttpServerErrorException("Original Predict Exception Message"));

    try {
      client.predict(mock(TimeSeriesAnalysisRequestModel.class));
      fail("should fail");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceRequestExceptionWithServerError(exception, "Failed on post predict to service:\nOriginal Predict Exception Message");
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