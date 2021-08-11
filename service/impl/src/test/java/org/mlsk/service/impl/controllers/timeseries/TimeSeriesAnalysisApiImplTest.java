package org.mlsk.service.impl.controllers.timeseries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.timeseries.model.TimeSeriesModel;
import org.mlsk.api.timeseries.model.TimeSeriesRowModel;
import org.mlsk.service.Engine;
import org.mlsk.service.impl.Orchestrator;
import org.mlsk.service.impl.exceptions.TimeSeriesAnalysisServiceException;
import org.mlsk.service.impl.mapper.timeseries.TimeSeriesModelHelper;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.model.timeseries.TimeSeriesRow;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.mlsk.service.utils.TimeSeriesAnalysisAlgorithmNames.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
public class TimeSeriesAnalysisApiImplTest {

  @Mock
  private Orchestrator orchestrator;
  @Mock
  private Engine engine;
  private TimeSeriesAnalysisApiImpl controller;

  @BeforeEach
  public void setup() {
    this.controller = new TimeSeriesAnalysisApiImpl(orchestrator);
  }

  @Test
  public void should_delegate_call_to_orchestrator_and_engine_on_forecast() {
    TimeSeriesAnalysisRequestModel model = buildTimeSeriesAnalysisRequestModel();
    onRunOnEngineCallMethod();
    onEngineForecastReturn(buildTimeSeriesResult());

    controller.forecast(model);

    verify(orchestrator).runOnEngine(any(), eq(TIME_SERIES_FORECAST));
    verify(engine).forecast(buildTimeSeriesAnalysisRequest());
  }

  @Test
  public void should_return_correct_response_on_forecast() {
    TimeSeriesAnalysisRequestModel model = buildTimeSeriesAnalysisRequestModel();
    onRunOnEngineCallMethod();
    onEngineForecastReturn(buildTimeSeriesResult());

    ResponseEntity<TimeSeriesModel> actualResponse = controller.forecast(model);

    assertEquals(OK, actualResponse.getStatusCode());
    assertEquals(buildTimeSeriesModelResult(), actualResponse.getBody());
  }

  @Test
  public void should_throw_time_series_analysis_service_exception_on_forecast_failure() {
    TimeSeriesAnalysisRequestModel model = buildTimeSeriesAnalysisRequestModel();
    doThrowExceptionOnRunOnEngine("exception message");

    try {
      controller.forecast(model);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceException(exception, "exception message");
    }
  }

  @Test
  public void should_delegate_call_to_orchestrator_and_engine_on_forecast_vs_actual() {
    TimeSeriesAnalysisRequestModel model = buildTimeSeriesAnalysisRequestModel();
    onRunOnEngineCallMethod();
    onEngineForecastVsActualReturn(buildTimeSeriesResult());

    controller.forecastVsActual(model);

    verify(orchestrator).runOnEngine(any(), eq(TIME_SERIES_FORECAST_VS_ACTUAL));
    verify(engine).forecastVsActual(buildTimeSeriesAnalysisRequest());
  }

  @Test
  public void should_return_correct_response_on_forecast_vs_actual() {
    TimeSeriesAnalysisRequestModel model = buildTimeSeriesAnalysisRequestModel();
    onRunOnEngineCallMethod();
    onEngineForecastVsActualReturn(buildTimeSeriesResult());

    ResponseEntity<TimeSeriesModel> actualResponse = controller.forecastVsActual(model);

    assertEquals(OK, actualResponse.getStatusCode());
    assertEquals(buildTimeSeriesModelResult(), actualResponse.getBody());
  }

  @Test
  public void should_throw_time_series_analysis_service_exception_on_forecast_vs_actual_failure() {
    TimeSeriesAnalysisRequestModel model = buildTimeSeriesAnalysisRequestModel();
    doThrowExceptionOnRunOnEngine("exception message for forecast vs actual");

    try {
      controller.forecastVsActual(model);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceException(exception, "exception message for forecast vs actual");
    }
  }

  @Test
  public void should_delegate_call_to_orchestrator_and_engine_on_compute_forecast_accuracy() {
    TimeSeriesAnalysisRequestModel model = buildTimeSeriesAnalysisRequestModel();
    onRunOnEngineCallMethod();
    onEngineComputeForecastAccuracyReturn(58.123);

    controller.computeForecastAccuracy(model);

    verify(orchestrator).runOnEngine(any(), eq(TIME_SERIES_FORECAST_ACCURACY));
    verify(engine).computeForecastAccuracy(buildTimeSeriesAnalysisRequest());
  }

  @Test
  public void should_return_correct_response_on_compute_forecast_accuracy() {
    TimeSeriesAnalysisRequestModel model = buildTimeSeriesAnalysisRequestModel();
    onRunOnEngineCallMethod();
    onEngineComputeForecastAccuracyReturn(1239.124);

    ResponseEntity<BigDecimal> actualResponse = controller.computeForecastAccuracy(model);

    assertEquals(OK, actualResponse.getStatusCode());
    assertEquals(valueOf(1239.124), actualResponse.getBody());
  }

  @Test
  public void should_throw_time_series_analysis_service_exception_on_compute_forecast_accuracy_failure() {
    TimeSeriesAnalysisRequestModel model = buildTimeSeriesAnalysisRequestModel();
    doThrowExceptionOnRunOnEngine("exception message for compute forecast accuracy");

    try {
      controller.computeForecastAccuracy(model);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceException(exception, "exception message for compute forecast accuracy");
    }
  }

  @Test
  public void should_delegate_call_to_orchestrator_and_engine_on_predict() {
    TimeSeriesAnalysisRequestModel model = buildTimeSeriesAnalysisRequestModel();
    onRunOnEngineCallMethod();
    onEnginePredictReturn(buildTimeSeriesResult());

    controller.predict(model);

    verify(orchestrator).runOnEngine(any(), eq(TIME_SERIES_PREDICT));
    verify(engine).predict(buildTimeSeriesAnalysisRequest());
  }

  @Test
  public void should_return_correct_response_on_predict() {
    TimeSeriesAnalysisRequestModel model = buildTimeSeriesAnalysisRequestModel();
    onRunOnEngineCallMethod();
    onEnginePredictReturn(buildTimeSeriesResult());

    ResponseEntity<TimeSeriesModel> actualResponse = controller.predict(model);

    assertEquals(OK, actualResponse.getStatusCode());
    assertEquals(buildTimeSeriesModelResult(), actualResponse.getBody());
  }

  @Test
  public void should_throw_time_series_analysis_service_exception_on_predict_failure() {
    TimeSeriesAnalysisRequestModel model = buildTimeSeriesAnalysisRequestModel();
    doThrowExceptionOnRunOnEngine("exception message for predict");

    try {
      controller.predict(model);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceException(exception, "exception message for predict");
    }
  }

  private void onRunOnEngineCallMethod() {
    when(orchestrator.runOnEngine(any(), any()))
        .thenAnswer(invocation -> {
          Function<Engine, ?> function = (Function<Engine, ?>) invocation.getArguments()[0];
          return function.apply(engine);
        });
  }

  private void doThrowExceptionOnRunOnEngine(String exceptionMessage) {
    doThrow(new RuntimeException(exceptionMessage)).when(orchestrator).runOnEngine(any(), any());
  }

  private void onEngineForecastReturn(TimeSeries timeSeries) {
    when(engine.forecast(any())).thenReturn(timeSeries);
  }

  private void onEngineForecastVsActualReturn(TimeSeries timeSeries) {
    when(engine.forecastVsActual(any())).thenReturn(timeSeries);
  }

  private void onEngineComputeForecastAccuracyReturn(Double accuracy) {
    when(engine.computeForecastAccuracy(any())).thenReturn(accuracy);
  }

  private void onEnginePredictReturn(TimeSeries timeSeries) {
    when(engine.predict(any())).thenReturn(timeSeries);
  }

  private static TimeSeriesAnalysisRequestModel buildTimeSeriesAnalysisRequestModel() {
    TimeSeriesRowModel row1 = TimeSeriesModelHelper.buildTimeSeriesRowModel("date1", valueOf(123.123123132));
    TimeSeriesRowModel row2 = TimeSeriesModelHelper.buildTimeSeriesRowModel("date2", valueOf(45454.31231));
    List<TimeSeriesRowModel> rows = newArrayList(row1, row2);

    TimeSeriesModel timeSeries = TimeSeriesModelHelper.buildTimeSeriesModel(rows, "dateColumnName", "valueColumnName", "yyyy");

    return TimeSeriesModelHelper.buildTimeSeriesAnalysisRequestModel(timeSeries, 2);
  }

  private static TimeSeriesAnalysisRequest buildTimeSeriesAnalysisRequest() {
    TimeSeriesRow row1 = new TimeSeriesRow("date1", 123.123123132);
    TimeSeriesRow row2 = new TimeSeriesRow("date2", 45454.31231);
    List<TimeSeriesRow> rows = newArrayList(row1, row2);

    TimeSeries timeSeries = new TimeSeries(rows, "dateColumnName", "valueColumnName", "yyyy");

    return new TimeSeriesAnalysisRequest(timeSeries, 2);
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
    ArrayList<TimeSeriesRowModel> rows = newArrayList(row1, row2);

    return TimeSeriesModelHelper.buildTimeSeriesModel(rows, "dateColumnName", "valueColumnName", "yyyy");
  }

  private static void assertOnTimeSeriesAnalysisServiceException(Exception exception, String exceptionMessage) {
    assertInstanceOf(TimeSeriesAnalysisServiceException.class, exception);
    assertEquals(exceptionMessage, exception.getMessage());
  }
}