package org.mlsk.service.impl.timeseries.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.service.Engine;
import org.mlsk.service.impl.orchestrator.Orchestrator;
import org.mlsk.service.impl.timeseries.service.exception.TimeSeriesAnalysisServiceException;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.model.timeseries.TimeSeriesRow;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mlsk.service.timeseries.utils.TimeSeriesAnalysisConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TimeSeriesAnalysisServiceImplTest {

  @Mock
  private Orchestrator orchestrator;
  @Mock
  private Engine engine;

  private TimeSeriesAnalysisServiceImpl service;

  @BeforeEach
  public void setUp() {
    this.service = new TimeSeriesAnalysisServiceImpl(orchestrator);
  }

  @Test
  public void should_delegate_call_to_orchestrator_and_engine_on_forecast() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    onRunOnEngineCallMethod();
    onEngineForecastReturn(buildTimeSeriesResult());

    service.forecast(request);

    InOrder inOrder = buildInOrder();
    inOrder.verify(orchestrator).runOnEngine(any(), eq(TIME_SERIES_FORECAST));
    inOrder.verify(engine).forecast(buildTimeSeriesAnalysisRequest());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_result_on_forecast() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    onRunOnEngineCallMethod();
    onEngineForecastReturn(buildTimeSeriesResult());

    TimeSeries actualForecast = service.forecast(request);

    assertEquals(buildTimeSeriesResult(), actualForecast);
  }

  @Test
  public void should_throw_time_series_analysis_service_exception_on_forecast_failure() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    doThrowExceptionOnRunOnEngine("exception message");

    try {
      service.forecast(request);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceException(exception, "exception message");
    }
  }

  @Test
  public void should_delegate_call_to_orchestrator_and_engine_on_forecast_vs_actual() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    onRunOnEngineCallMethod();
    onEngineForecastReturn(buildTimeSeriesResult());

    service.forecastVsActual(request);

    InOrder inOrder = buildInOrder();
    inOrder.verify(orchestrator).runOnEngine(any(), eq(TIME_SERIES_FORECAST_VS_ACTUAL));
    inOrder.verify(engine).forecast(buildExpectedRequestForForecastVsActual());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_result_on_forecast_vs_actual() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    onRunOnEngineCallMethod();
    onEngineForecastReturn(buildTimeSeriesResult());

    TimeSeries actualForecast = service.forecastVsActual(request);

    assertEquals(buildTimeSeriesResult(), actualForecast);
  }

  @Test
  public void should_throw_time_series_analysis_service_exception_on_forecast_vs_actual_failure() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    doThrowExceptionOnRunOnEngine("exception message for forecast vs actual");

    try {
      service.forecastVsActual(request);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceException(exception, "exception message for forecast vs actual");
    }
  }

  @Test
  public void should_delegate_call_to_orchestrator_and_engine_on_compute_forecast_accuracy() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    onRunOnEngineCallMethod();
    onEngineComputeForecastAccuracyReturn(58.123);

    service.computeForecastAccuracy(request);

    InOrder inOrder = buildInOrder();
    inOrder.verify(orchestrator).runOnEngine(any(), eq(TIME_SERIES_FORECAST_ACCURACY));
    inOrder.verify(engine).computeForecastAccuracy(buildTimeSeriesAnalysisRequest());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_result_on_compute_forecast_accuracy() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    onRunOnEngineCallMethod();
    onEngineComputeForecastAccuracyReturn(1239.124);

    Double actualAccuracy = service.computeForecastAccuracy(request);

    assertEquals(1239.124, actualAccuracy);
  }

  @Test
  public void should_throw_time_series_analysis_service_exception_on_compute_forecast_accuracy_failure() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    doThrowExceptionOnRunOnEngine("exception message for compute forecast accuracy");

    try {
      service.computeForecastAccuracy(request);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceException(exception, "exception message for compute forecast accuracy");
    }
  }

  @Test
  public void should_delegate_call_to_orchestrator_and_engine_on_predict() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    onRunOnEngineCallMethod();
    onEnginePredictReturn(buildTimeSeriesResult());

    service.predict(request);

    InOrder inOrder = buildInOrder();
    inOrder.verify(orchestrator).runOnEngine(any(), eq(TIME_SERIES_PREDICT));
    inOrder.verify(engine).predict(buildTimeSeriesAnalysisRequest());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_result_on_predict() {
    TimeSeriesAnalysisRequest model = buildTimeSeriesAnalysisRequest();
    onRunOnEngineCallMethod();
    onEnginePredictReturn(buildTimeSeriesResult());

    TimeSeries actualPredict = service.predict(model);

    assertEquals(buildTimeSeriesResult(), actualPredict);
  }

  @Test
  public void should_throw_time_series_analysis_service_exception_on_predict_failure() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    doThrowExceptionOnRunOnEngine("exception message for predict");

    try {
      service.predict(request);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceException(exception, "exception message for predict");
    }
  }

  private InOrder buildInOrder() {
    return inOrder(orchestrator, engine);
  }

  @SuppressWarnings("unchecked")
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

  private void onEngineComputeForecastAccuracyReturn(Double accuracy) {
    when(engine.computeForecastAccuracy(any())).thenReturn(accuracy);
  }

  private void onEnginePredictReturn(TimeSeries timeSeries) {
    when(engine.predict(any())).thenReturn(timeSeries);
  }

  private static TimeSeriesAnalysisRequest buildTimeSeriesAnalysisRequest() {
    TimeSeriesRow row1 = new TimeSeriesRow("date1", 123.123123132);
    TimeSeriesRow row2 = new TimeSeriesRow("date2", 45454.31231);
    TimeSeriesRow row3 = new TimeSeriesRow("date3", 543.638);
    TimeSeriesRow row4 = new TimeSeriesRow("date4", 8492.12341);
    List<TimeSeriesRow> rows = newArrayList(row1, row2, row3, row4);

    TimeSeries timeSeries = new TimeSeries(rows, "dateColumnName", "valueColumnName", "yyyy");

    return new TimeSeriesAnalysisRequest(timeSeries, 2);
  }

  private static TimeSeries buildTimeSeriesResult() {
    TimeSeriesRow row1 = new TimeSeriesRow("date3", 77272.123);
    TimeSeriesRow row2 = new TimeSeriesRow("date4", 989823.124);
    List<TimeSeriesRow> rows = newArrayList(row1, row2);
    return new TimeSeries(rows, "dateColumnName", "valueColumnName", "yyyy");
  }

  private static TimeSeriesAnalysisRequest buildExpectedRequestForForecastVsActual() {
    TimeSeriesRow row1 = new TimeSeriesRow("date1", 123.123123132);
    TimeSeriesRow row2 = new TimeSeriesRow("date2", 45454.31231);
    List<TimeSeriesRow> rows = newArrayList(row1, row2);

    TimeSeries timeSeries = new TimeSeries(rows, "dateColumnName", "valueColumnName", "yyyy");

    return new TimeSeriesAnalysisRequest(timeSeries, 2);
  }

  private static void assertOnTimeSeriesAnalysisServiceException(Exception exception, String exceptionMessage) {
    assertInstanceOf(TimeSeriesAnalysisServiceException.class, exception);
    assertEquals(exceptionMessage, exception.getMessage());
  }
}