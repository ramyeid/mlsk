package org.mlsk.service.impl.timeseries.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.service.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.service.timeseries.model.TimeSeriesModel;
import org.mlsk.api.service.timeseries.model.TimeSeriesRowModel;
import org.mlsk.service.impl.orchestrator.request.generator.RequestIdGenerator;
import org.mlsk.service.impl.timeseries.mapper.TimeSeriesModelHelper;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.model.timeseries.TimeSeriesRow;
import org.mlsk.service.timeseries.TimeSeriesAnalysisService;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.math.BigDecimal.valueOf;
import static org.mlsk.service.impl.testhelper.ResponseEntityHelper.assertOnResponseEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TimeSeriesAnalysisApiImplTest {

  @Mock
  private TimeSeriesAnalysisService service;

  private TimeSeriesAnalysisApiImpl timeSeriesAnalysisApi;

  @BeforeEach
  public void setUp() {
    this.timeSeriesAnalysisApi = new TimeSeriesAnalysisApiImpl(service);
    RequestIdGenerator.reset(1L);
  }

  @Test
  public void should_delegate_call_to_service_on_forecast() {
    long requestId = 1L;
    TimeSeriesAnalysisRequestModel model = buildTimeSeriesAnalysisRequestModel();
    onServiceForecastReturn(buildTimeSeriesResult());

    timeSeriesAnalysisApi.forecast(model);

    InOrder inOrder = buildInOrder();
    inOrder.verify(service).forecast(buildTimeSeriesAnalysisRequest(requestId));
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_response_on_forecast() {
    TimeSeriesAnalysisRequestModel model = buildTimeSeriesAnalysisRequestModel();
    onServiceForecastReturn(buildTimeSeriesResult());

    ResponseEntity<TimeSeriesModel> actualResponse = timeSeriesAnalysisApi.forecast(model);

    assertOnResponseEntity(buildTimeSeriesModelResult(), actualResponse);
  }

  @Test
  public void should_delegate_call_to_service_on_forecast_vs_actual() {
    long requestId = 1L;
    TimeSeriesAnalysisRequestModel model = buildTimeSeriesAnalysisRequestModel();
    onServiceForecastVsActualReturn(buildTimeSeriesResult());

    timeSeriesAnalysisApi.forecastVsActual(model);

    InOrder inOrder = buildInOrder();
    inOrder.verify(service).forecastVsActual(buildTimeSeriesAnalysisRequest(requestId));
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_response_on_forecast_vs_actual() {
    TimeSeriesAnalysisRequestModel model = buildTimeSeriesAnalysisRequestModel();
    onServiceForecastVsActualReturn(buildTimeSeriesResult());

    ResponseEntity<TimeSeriesModel> actualResponse = timeSeriesAnalysisApi.forecastVsActual(model);

    assertOnResponseEntity(buildTimeSeriesModelResult(), actualResponse);
  }

  @Test
  public void should_delegate_call_to_service_on_compute_forecast_accuracy() {
    long requestId = 1L;
    TimeSeriesAnalysisRequestModel model = buildTimeSeriesAnalysisRequestModel();
    onServiceComputeForecastAccuracyReturn(58.123);

    timeSeriesAnalysisApi.computeForecastAccuracy(model);

    InOrder inOrder = buildInOrder();
    inOrder.verify(service).computeForecastAccuracy(buildTimeSeriesAnalysisRequest(requestId));
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_response_on_compute_forecast_accuracy() {
    TimeSeriesAnalysisRequestModel model = buildTimeSeriesAnalysisRequestModel();
    onServiceComputeForecastAccuracyReturn(1239.124);

    ResponseEntity<BigDecimal> actualResponse = timeSeriesAnalysisApi.computeForecastAccuracy(model);

    assertOnResponseEntity(valueOf(1239.124), actualResponse);
  }

  @Test
  public void should_delegate_call_to_service_on_predict() {
    long requestId = 1L;
    TimeSeriesAnalysisRequestModel model = buildTimeSeriesAnalysisRequestModel();
    onServicePredictReturn(buildTimeSeriesResult());

    timeSeriesAnalysisApi.predict(model);

    InOrder inOrder = buildInOrder();
    inOrder.verify(service).predict(buildTimeSeriesAnalysisRequest(requestId));
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_response_on_predict() {
    TimeSeriesAnalysisRequestModel model = buildTimeSeriesAnalysisRequestModel();
    onServicePredictReturn(buildTimeSeriesResult());

    ResponseEntity<TimeSeriesModel> actualResponse = timeSeriesAnalysisApi.predict(model);

    assertOnResponseEntity(buildTimeSeriesModelResult(), actualResponse);
  }

  private InOrder buildInOrder() {
    return inOrder(service);
  }

  private void onServiceForecastReturn(TimeSeries timeSeries) {
    when(service.forecast(any())).thenReturn(timeSeries);
  }

  private void onServiceForecastVsActualReturn(TimeSeries timeSeries) {
    when(service.forecastVsActual(any())).thenReturn(timeSeries);
  }

  private void onServiceComputeForecastAccuracyReturn(Double accuracy) {
    when(service.computeForecastAccuracy(any())).thenReturn(accuracy);
  }

  private void onServicePredictReturn(TimeSeries timeSeries) {
    when(service.predict(any())).thenReturn(timeSeries);
  }

  private static TimeSeriesAnalysisRequestModel buildTimeSeriesAnalysisRequestModel() {
    TimeSeriesRowModel row1 = TimeSeriesModelHelper.buildTimeSeriesRowModel("date1", valueOf(123.123123132));
    TimeSeriesRowModel row2 = TimeSeriesModelHelper.buildTimeSeriesRowModel("date2", valueOf(45454.31231));
    List<TimeSeriesRowModel> rows = newArrayList(row1, row2);

    TimeSeriesModel timeSeries = TimeSeriesModelHelper.buildTimeSeriesModel(rows, "dateColumnName", "valueColumnName", "yyyy");

    return TimeSeriesModelHelper.buildTimeSeriesAnalysisRequestModel(timeSeries, 2);
  }

  private static TimeSeriesAnalysisRequest buildTimeSeriesAnalysisRequest(long requestId) {
    TimeSeriesRow row1 = new TimeSeriesRow("date1", 123.123123132);
    TimeSeriesRow row2 = new TimeSeriesRow("date2", 45454.31231);
    List<TimeSeriesRow> rows = newArrayList(row1, row2);

    TimeSeries timeSeries = new TimeSeries(rows, "dateColumnName", "valueColumnName", "yyyy");

    return new TimeSeriesAnalysisRequest(requestId, timeSeries, 2);
  }

  private static TimeSeries buildTimeSeriesResult() {
    TimeSeriesRow row1 = new TimeSeriesRow("date3", 77272.123);
    TimeSeriesRow row2 = new TimeSeriesRow("date4", 989823.124);
    List<TimeSeriesRow> rows = newArrayList(row1, row2);
    return new TimeSeries(rows, "dateColumnName", "valueColumnName", "yyyy");
  }

  private static TimeSeriesModel buildTimeSeriesModelResult() {
    TimeSeriesRowModel row1 = TimeSeriesModelHelper.buildTimeSeriesRowModel("date3", valueOf(77272.123));
    TimeSeriesRowModel row2 = TimeSeriesModelHelper.buildTimeSeriesRowModel("date4", valueOf(989823.124));
    List<TimeSeriesRowModel> rows = newArrayList(row1, row2);

    return TimeSeriesModelHelper.buildTimeSeriesModel(rows, "dateColumnName", "valueColumnName", "yyyy");
  }
}