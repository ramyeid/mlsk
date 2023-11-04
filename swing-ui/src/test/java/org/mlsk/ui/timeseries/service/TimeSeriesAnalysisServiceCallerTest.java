package org.mlsk.ui.timeseries.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.timeseries.model.TimeSeriesModel;
import org.mlsk.api.timeseries.model.TimeSeriesRowModel;
import org.mlsk.ui.timeseries.service.client.TimeSeriesAnalysisServiceClient;
import org.mlsk.ui.timeseries.service.client.exception.TimeSeriesAnalysisServiceRequestException;
import org.mlsk.ui.utils.TriFunction;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
  private TriFunction<TimeSeriesModel, Object, String> onResults;

  private TimeSeriesAnalysisServiceCaller serviceCaller;

  @BeforeEach
  public void setUp() {
    this.serviceCaller = new TimeSeriesAnalysisServiceCaller(serviceClient, onResults);
  }

  @Test
  public void should_delegate_call_to_service_on_predict() {
    TimeSeriesAnalysisRequestModel request = buildTimeSeriesAnalysisRequest();
    onPredictReturn(buildTimeSeriesResult());

    serviceCaller.callService(PREDICT, request);

    InOrder inOrder = buildInOrder();
    inOrder.verify(serviceClient).predict(buildTimeSeriesAnalysisRequest());
    inOrder.verify(onResults).apply(buildTimeSeriesRequest(), buildTimeSeriesResult(), "predict");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_exception_if_service_call_fail_on_predict() {
    TimeSeriesAnalysisRequestModel request = buildTimeSeriesAnalysisRequest();
    doThrowOnPredict(new TimeSeriesAnalysisServiceRequestException("errorMessage", new UnsupportedOperationException()));

    try {
      serviceCaller.callService(PREDICT, request);
      fail("should throw exception since service call failed");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceException.class, exception);
      assertInstanceOf(TimeSeriesAnalysisServiceRequestException.class, exception.getCause());
      assertEquals("org.mlsk.ui.timeseries.service.client.exception.TimeSeriesAnalysisServiceRequestException: errorMessage", exception.getMessage());
    }
  }

  @Test
  public void should_delegate_call_to_service_on_forecast() {
    TimeSeriesAnalysisRequestModel request = buildTimeSeriesAnalysisRequest();
    onForecastReturn(buildTimeSeriesResult());

    serviceCaller.callService(FORECAST, request);

    InOrder inOrder = buildInOrder();
    inOrder.verify(serviceClient).forecast(buildTimeSeriesAnalysisRequest());
    inOrder.verify(onResults).apply(buildTimeSeriesRequest(), buildTimeSeriesResult(), "forecast");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_exception_if_service_call_fail_on_forecast() {
    TimeSeriesAnalysisRequestModel request = buildTimeSeriesAnalysisRequest();
    doThrowOnForecast(new TimeSeriesAnalysisServiceRequestException("errorMessage", new UnsupportedOperationException()));

    try {
      serviceCaller.callService(FORECAST, request);
      fail("should throw exception since service call failed");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceException.class, exception);
      assertInstanceOf(TimeSeriesAnalysisServiceRequestException.class, exception.getCause());
      assertEquals("org.mlsk.ui.timeseries.service.client.exception.TimeSeriesAnalysisServiceRequestException: errorMessage", exception.getMessage());
    }
  }

  @Test
  public void should_delegate_call_to_service_on_forecast_vs_actual() {
    TimeSeriesAnalysisRequestModel request = buildTimeSeriesAnalysisRequest();
    onForecastVsActualReturn(buildTimeSeriesResult());

    serviceCaller.callService(FORECAST_VS_ACTUAL, request);

    InOrder inOrder = buildInOrder();
    inOrder.verify(serviceClient).forecastVsActual(buildTimeSeriesAnalysisRequest());
    inOrder.verify(onResults).apply(buildTimeSeriesRequest(), buildTimeSeriesResult(), "forecast");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_exception_if_service_call_fail_on_forecast_vs_Actual() {
    TimeSeriesAnalysisRequestModel request = buildTimeSeriesAnalysisRequest();
    doThrowOnForecastVsActual(new TimeSeriesAnalysisServiceRequestException("errorMessage", new UnsupportedOperationException()));

    try {
      serviceCaller.callService(FORECAST_VS_ACTUAL, request);
      fail("should throw exception since service call failed");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceException.class, exception);
      assertInstanceOf(TimeSeriesAnalysisServiceRequestException.class, exception.getCause());
      assertEquals("org.mlsk.ui.timeseries.service.client.exception.TimeSeriesAnalysisServiceRequestException: errorMessage", exception.getMessage());
    }
  }

  @Test
  public void should_delegate_call_to_service_on_forecast_accuracy() {
    TimeSeriesAnalysisRequestModel request = buildTimeSeriesAnalysisRequest();
    onForecastAccuracyReturn(96.4);

    serviceCaller.callService(FORECAST_ACCURACY, request);

    InOrder inOrder = buildInOrder();
    inOrder.verify(serviceClient).computeForecastAccuracy(buildTimeSeriesAnalysisRequest());
    inOrder.verify(onResults).apply(buildTimeSeriesRequest(), BigDecimal.valueOf(96.4), "forecast accuracy");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_exception_if_service_call_fail_on_forecast_accuracy() {
    TimeSeriesAnalysisRequestModel request = buildTimeSeriesAnalysisRequest();
    doThrowOnForecastAccuracy(new TimeSeriesAnalysisServiceRequestException("errorMessage", new UnsupportedOperationException()));

    try {
      serviceCaller.callService(FORECAST_ACCURACY, request);
      fail("should throw exception since service call failed");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceException.class, exception);
      assertInstanceOf(TimeSeriesAnalysisServiceRequestException.class, exception.getCause());
      assertEquals("org.mlsk.ui.timeseries.service.client.exception.TimeSeriesAnalysisServiceRequestException: errorMessage", exception.getMessage());
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

  private void onPredictReturn(TimeSeriesModel timeSeries) {
    when(serviceClient.predict(any())).thenReturn(timeSeries);
  }

  private void onForecastReturn(TimeSeriesModel timeSeries) {
    when(serviceClient.forecast(any())).thenReturn(timeSeries);
  }

  private void onForecastVsActualReturn(TimeSeriesModel timeSeries) {
    when(serviceClient.forecastVsActual(any())).thenReturn(timeSeries);
  }

  private void onForecastAccuracyReturn(Double accuracy) {
    when(serviceClient.computeForecastAccuracy(any())).thenReturn(BigDecimal.valueOf(accuracy));
  }

  private static TimeSeriesModel buildTimeSeriesResult() {
    TimeSeriesRowModel row = new TimeSeriesRowModel().date("1989").value(BigDecimal.valueOf(1.));
    List<TimeSeriesRowModel> rows = newArrayList(row);

    return new TimeSeriesModel()
        .rows(rows)
        .dateColumnName("date")
        .valueColumnName("value")
        .dateFormat("yyyy");
  }

  private static TimeSeriesModel buildTimeSeriesRequest() {
    TimeSeriesRowModel row = new TimeSeriesRowModel().date("1990").value(BigDecimal.valueOf(5.));
    List<TimeSeriesRowModel> rows = newArrayList(row);

    return new TimeSeriesModel()
        .rows(rows)
        .dateColumnName("date")
        .valueColumnName("value")
        .dateFormat("yyyy");
  }

  private static TimeSeriesAnalysisRequestModel buildTimeSeriesAnalysisRequest() {
    TimeSeriesModel timeSeries = buildTimeSeriesRequest();

    return new TimeSeriesAnalysisRequestModel().timeSeries(timeSeries).numberOfValues(1);
  }

}