package org.mlsk.ui.timeseries.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.model.timeseries.TimeSeriesRow;
import org.mlsk.ui.timeseries.service.client.TimeSeriesAnalysisServiceClient;
import org.mlsk.ui.timeseries.service.client.exception.TimeSeriesAnalysisServiceRequestException;
import org.mlsk.ui.utils.TriFunction;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mlsk.ui.timeseries.service.TimeSeriesAnalysisCommand.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TimeSeriesAnalysisServiceCallerTest {

  @Mock
  private TimeSeriesAnalysisServiceClient serviceClient;
  @Mock
  private TriFunction<TimeSeries, Object, String> onResults;

  private TimeSeriesAnalysisServiceCaller serviceCaller;

  @BeforeEach
  public void setUp() {
    this.serviceCaller = new TimeSeriesAnalysisServiceCaller(serviceClient, onResults);
  }

  @Test
  public void should_delegate_call_to_service_on_predict() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    onPredictReturn(buildTimeSeriesResult());

    serviceCaller.callService(PREDICT, request);

    InOrder inOrder = buildInOrder();
    inOrder.verify(serviceClient).predict(buildTimeSeriesAnalysisRequest());
    inOrder.verify(onResults).apply(buildTimeSeriesRequest(), buildTimeSeriesResult(), "predict");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_exception_if_service_call_fail_on_predict() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    doThrowOnPredict(new TimeSeriesAnalysisServiceRequestException("errorMessage", new UnsupportedOperationException()));

    try {
      serviceCaller.callService(PREDICT, request);
      fail("should throw exception since service call failed");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceException.class, exception);
      assertInstanceOf(TimeSeriesAnalysisServiceRequestException.class, exception.getCause());
      assertEquals("errorMessage", exception.getMessage());
    }
  }

  @Test
  public void should_delegate_call_to_service_on_forecast() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    onForecastReturn(buildTimeSeriesResult());

    serviceCaller.callService(FORECAST, request);

    InOrder inOrder = buildInOrder();
    inOrder.verify(serviceClient).forecast(buildTimeSeriesAnalysisRequest());
    inOrder.verify(onResults).apply(buildTimeSeriesRequest(), buildTimeSeriesResult(), "forecast");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_exception_if_service_call_fail_on_forecast() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    doThrowOnForecast(new TimeSeriesAnalysisServiceRequestException("errorMessage", new UnsupportedOperationException()));

    try {
      serviceCaller.callService(FORECAST, request);
      fail("should throw exception since service call failed");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceException.class, exception);
      assertInstanceOf(TimeSeriesAnalysisServiceRequestException.class, exception.getCause());
      assertEquals("errorMessage", exception.getMessage());
    }
  }

  @Test
  public void should_delegate_call_to_service_on_forecast_vs_actual() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    onForecastVsActualReturn(buildTimeSeriesResult());

    serviceCaller.callService(FORECAST_VS_ACTUAL, request);

    InOrder inOrder = buildInOrder();
    inOrder.verify(serviceClient).forecastVsActual(buildTimeSeriesAnalysisRequest());
    inOrder.verify(onResults).apply(buildTimeSeriesRequest(), buildTimeSeriesResult(), "forecast");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_exception_if_service_call_fail_on_forecast_vs_Actual() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    doThrowOnForecastVsActual(new TimeSeriesAnalysisServiceRequestException("errorMessage", new UnsupportedOperationException()));

    try {
      serviceCaller.callService(FORECAST_VS_ACTUAL, request);
      fail("should throw exception since service call failed");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceException.class, exception);
      assertInstanceOf(TimeSeriesAnalysisServiceRequestException.class, exception.getCause());
      assertEquals("errorMessage", exception.getMessage());
    }
  }

  @Test
  public void should_delegate_call_to_service_on_forecast_accuracy() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    onForecastAccuracyReturn(96.4);

    serviceCaller.callService(FORECAST_ACCURACY, request);

    InOrder inOrder = buildInOrder();
    inOrder.verify(serviceClient).computeForecastAccuracy(buildTimeSeriesAnalysisRequest());
    inOrder.verify(onResults).apply(buildTimeSeriesRequest(),96.4, "forecast accuracy");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_exception_if_service_call_fail_on_forecast_accuracy() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    doThrowOnForecastAccuracy(new TimeSeriesAnalysisServiceRequestException("errorMessage", new UnsupportedOperationException()));

    try {
      serviceCaller.callService(FORECAST_ACCURACY, request);
      fail("should throw exception since service call failed");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceException.class, exception);
      assertInstanceOf(TimeSeriesAnalysisServiceRequestException.class, exception.getCause());
      assertEquals("errorMessage", exception.getMessage());
    }
  }

  private InOrder buildInOrder() {
    return inOrder(serviceClient, onResults);
  }

  private void doThrowOnPredict(TimeSeriesAnalysisServiceRequestException exception) {
    doThrow(exception).when(serviceClient).predict(any());
  }

  private void doThrowOnForecast(TimeSeriesAnalysisServiceRequestException exception) {
    doThrow(exception).when(serviceClient).forecast(any());
  }

  private void doThrowOnForecastVsActual(TimeSeriesAnalysisServiceRequestException exception) {
    doThrow(exception).when(serviceClient).forecastVsActual(any());
  }

  private void doThrowOnForecastAccuracy(TimeSeriesAnalysisServiceRequestException exception) {
    doThrow(exception).when(serviceClient).computeForecastAccuracy(any());
  }

  private void onPredictReturn(TimeSeries timeSeries) {
    when(serviceClient.predict(any())).thenReturn(timeSeries);
  }

  private void onForecastReturn(TimeSeries timeSeries) {
    when(serviceClient.forecast(any())).thenReturn(timeSeries);
  }

  private void onForecastVsActualReturn(TimeSeries timeSeries) {
    when(serviceClient.forecastVsActual(any())).thenReturn(timeSeries);
  }

  private void onForecastAccuracyReturn(Double accuracy) {
    when(serviceClient.computeForecastAccuracy(any())).thenReturn(accuracy);
  }

  private static TimeSeries buildTimeSeriesResult() {
    TimeSeriesRow row = new TimeSeriesRow("1989", 1.);
    List<TimeSeriesRow> rows = newArrayList(row);

    return new TimeSeries(rows, "date", "value", "yyyy");
  }

  private static TimeSeries buildTimeSeriesRequest() {
    TimeSeriesRow row = new TimeSeriesRow("1990", 5.);
    List<TimeSeriesRow> rows = newArrayList(row);

    return new TimeSeries(rows, "date", "value", "yyyy");
  }

  private static TimeSeriesAnalysisRequest buildTimeSeriesAnalysisRequest() {
    TimeSeries timeSeries = buildTimeSeriesRequest();

    return new TimeSeriesAnalysisRequest(timeSeries, 1);
  }

}