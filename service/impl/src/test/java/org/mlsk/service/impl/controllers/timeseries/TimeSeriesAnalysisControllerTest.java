package org.mlsk.service.impl.controllers.timeseries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.service.Engine;
import org.mlsk.service.impl.Orchestrator;
import org.mlsk.service.impl.exceptions.TimeSeriesAnalysisServiceException;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mlsk.service.utils.TimeSeriesAnalysisAlgorithmNames.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TimeSeriesAnalysisControllerTest {

  @Mock
  private Orchestrator orchestrator;
  @Mock
  private Engine engine;
  private TimeSeriesAnalysisController controller;

  @BeforeEach
  public void setup() {
    this.controller = new TimeSeriesAnalysisController(orchestrator);
  }

  @Test
  public void should_delegate_call_to_orchestrator_and_engine_on_forecast() {
    TimeSeriesAnalysisRequest mock = mock(TimeSeriesAnalysisRequest.class);
    onRunOnEngineCallMethod();

    controller.forecast(mock);

    verify(orchestrator).runOnEngine(any(), eq(TIME_SERIES_FORECAST));
    verify(engine).forecast(mock);
  }

  @Test
  public void should_throw_time_series_analysis_service_exception_on_forecast_failure() {
    TimeSeriesAnalysisRequest mock = mock(TimeSeriesAnalysisRequest.class);
    doThrowExceptionOnRunOnEngine("exception message");

    try {
      controller.forecast(mock);
      fail("should throw exception");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceException.class, exception);
      assertEquals("exception message", exception.getMessage());
    }
  }

  @Test
  public void should_delegate_call_to_orchestrator_and_engine_on_forecast_vs_actual() {
    TimeSeriesAnalysisRequest mock = mock(TimeSeriesAnalysisRequest.class);
    onRunOnEngineCallMethod();

    controller.forecastVsActual(mock);

    verify(orchestrator).runOnEngine(any(), eq(TIME_SERIES_FORECAST_VS_ACTUAL));
    verify(engine).forecastVsActual(mock);
  }

  @Test
  public void should_throw_time_series_analysis_service_exception_on_forecast_vs_actual_failure() {
    TimeSeriesAnalysisRequest mock = mock(TimeSeriesAnalysisRequest.class);
    doThrowExceptionOnRunOnEngine("exception message for forecast vs actual");

    try {
      controller.forecastVsActual(mock);
      fail("should throw exception");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceException.class, exception);
      assertEquals("exception message for forecast vs actual", exception.getMessage());
    }
  }

  @Test
  public void should_delegate_call_to_orchestrator_and_engine_on_compute_forecast_accuracy() {
    TimeSeriesAnalysisRequest mock = mock(TimeSeriesAnalysisRequest.class);
    onRunOnEngineCallMethod();

    controller.computeForecastAccuracy(mock);

    verify(orchestrator).runOnEngine(any(), eq(TIME_SERIES_FORECAST_ACCURACY));
    verify(engine).computeForecastAccuracy(mock);
  }

  @Test
  public void should_throw_time_series_analysis_service_exception_on_compute_forecast_accuracy_failure() {
    TimeSeriesAnalysisRequest mock = mock(TimeSeriesAnalysisRequest.class);
    doThrowExceptionOnRunOnEngine("exception message for compute forecast accuracy");

    try {
      controller.computeForecastAccuracy(mock);
      fail("should throw exception");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceException.class, exception);
      assertEquals("exception message for compute forecast accuracy", exception.getMessage());
    }
  }

  @Test
  public void should_delegate_call_to_orchestrator_and_engine_on_predict() {
    TimeSeriesAnalysisRequest mock = mock(TimeSeriesAnalysisRequest.class);
    onRunOnEngineCallMethod();

    controller.predict(mock);

    verify(orchestrator).runOnEngine(any(), eq(TIME_SERIES_PREDICT));
    verify(engine).predict(mock);
  }


  @Test
  public void should_throw_time_series_analysis_service_exception_on_predict_failure() {
    TimeSeriesAnalysisRequest mock = mock(TimeSeriesAnalysisRequest.class);
    doThrowExceptionOnRunOnEngine("exception message for predict");

    try {
      controller.predict(mock);
      fail("should throw exception");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceException.class, exception);
      assertEquals("exception message for predict", exception.getMessage());
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

}