package org.mlsk.service.impl.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.lib.engine.ResilientEngine;
import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.service.impl.engine.impl.EngineImpl;
import org.mlsk.service.impl.engine.impl.timeseries.TimeSeriesAnalysisEngineCaller;
import org.mlsk.service.model.EngineState;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.model.timeseries.TimeSeriesRow;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.model.EngineState.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EngineImplTest {

  private static final ServiceInformation SERVICE_INFORMATION = new ServiceInformation("host", "port");

  @Mock
  private ResilientEngine resilientEngine;
  @Mock
  private TimeSeriesAnalysisEngineCaller timeSeriesAnalysisEngineCaller;

  private AtomicReference<EngineState> engineStateSpy;
  private EngineImpl engineImpl;

  @BeforeEach
  public void setUp() {
    engineStateSpy = spy(new AtomicReference<>());
    engineImpl = new EngineImpl(resilientEngine, SERVICE_INFORMATION, engineStateSpy, timeSeriesAnalysisEngineCaller);
  }

  @Test
  public void should_modify_state_and_relaunch_engine_when_killed() throws IOException, InterruptedException {

    engineImpl.onProcessKilled();

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineStateSpy).set(OFF);
    inOrder.verify(resilientEngine).launchEngine();
    inOrder.verify(engineStateSpy).set(WAITING);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_keep_state_off_when_engine_fail_to_relaunch() throws IOException, InterruptedException {
    doThrow(new RuntimeException("Exception while relaunch")).when(resilientEngine).launchEngine();

    engineImpl.onProcessKilled();

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineStateSpy).set(OFF);
    inOrder.verify(resilientEngine).launchEngine();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_delegate_forecast_call_to_engine() {
    onForecastReturn(buildTimeSeriesAnalysisRequest(), buildTimeSeries());

    engineImpl.forecast(buildTimeSeriesAnalysisRequest());

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineStateSpy).set(COMPUTING);
    inOrder.verify(timeSeriesAnalysisEngineCaller).forecast(buildTimeSeriesAnalysisRequest());
    inOrder.verify(engineStateSpy).set(WAITING);
    inOrder.verifyNoMoreInteractions();

  }

  @Test
  public void should_return_time_series_and_reset_state_on_forecast() {
    onForecastReturn(buildTimeSeriesAnalysisRequest(), buildTimeSeries());

    TimeSeries actualTimeSeries = engineImpl.forecast(buildTimeSeriesAnalysisRequest());

    assertEquals(buildTimeSeries(), actualTimeSeries);
    assertEquals(WAITING, engineImpl.getState());
  }

  @Test
  public void should_delegate_forecast_vs_actual_call_to_engine() {
    onForecastVsActualReturn(buildTimeSeriesAnalysisRequest(), buildTimeSeries());

    engineImpl.forecastVsActual(buildTimeSeriesAnalysisRequest());

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineStateSpy).set(COMPUTING);
    inOrder.verify(timeSeriesAnalysisEngineCaller).forecastVsActual(buildTimeSeriesAnalysisRequest());
    inOrder.verify(engineStateSpy).set(WAITING);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_time_series_and_reset_state_on_forecast_vs_actual() {
    onForecastVsActualReturn(buildTimeSeriesAnalysisRequest(), buildTimeSeries());

    TimeSeries actualTimeSeries = engineImpl.forecastVsActual(buildTimeSeriesAnalysisRequest());

    assertEquals(buildTimeSeries(), actualTimeSeries);
    assertEquals(WAITING, engineImpl.getState());
  }

  @Test
  public void should_delegate_compute_forecast_accuracy_to_engine() {
    onComputeForecastAccuracyReturn(buildTimeSeriesAnalysisRequest(), 2.0);

    engineImpl.computeForecastAccuracy(buildTimeSeriesAnalysisRequest());

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineStateSpy).set(COMPUTING);
    inOrder.verify(timeSeriesAnalysisEngineCaller).computeForecastAccuracy(buildTimeSeriesAnalysisRequest());
    inOrder.verify(engineStateSpy).set(WAITING);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_accuracy_and_reset_state_on_compute_forecast_accuracy() {
    onComputeForecastAccuracyReturn(buildTimeSeriesAnalysisRequest(), 3.0);

    Double actualAccuracy = engineImpl.computeForecastAccuracy(buildTimeSeriesAnalysisRequest());

    assertEquals(3.0, actualAccuracy);
    assertEquals(WAITING, engineImpl.getState());
  }

  @Test
  public void should_delegate_predict_call_to_engine() {
    onPredictReturn(buildTimeSeriesAnalysisRequest(), buildTimeSeries());

    engineImpl.predict(buildTimeSeriesAnalysisRequest());

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineStateSpy).set(COMPUTING);
    inOrder.verify(timeSeriesAnalysisEngineCaller).predict(buildTimeSeriesAnalysisRequest());
    inOrder.verify(engineStateSpy).set(WAITING);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_time_series_and_reset_state_on_preidct() {
    onPredictReturn(buildTimeSeriesAnalysisRequest(), buildTimeSeries());

    TimeSeries actualTimeSeries = engineImpl.predict(buildTimeSeriesAnalysisRequest());

    assertEquals(buildTimeSeries(), actualTimeSeries);
    assertEquals(WAITING, engineImpl.getState());
  }

  private InOrder buildInOrder() {
    return inOrder(timeSeriesAnalysisEngineCaller, engineStateSpy, resilientEngine);
  }

  private void onForecastReturn(TimeSeriesAnalysisRequest request, TimeSeries timeSeries) {
    when(timeSeriesAnalysisEngineCaller.forecast(request)).thenReturn(timeSeries);
  }

  private void onForecastVsActualReturn(TimeSeriesAnalysisRequest request, TimeSeries timeSeries) {
    when(timeSeriesAnalysisEngineCaller.forecastVsActual(request)).thenReturn(timeSeries);
  }

  private void onComputeForecastAccuracyReturn(TimeSeriesAnalysisRequest request, Double accuracy) {
    when(timeSeriesAnalysisEngineCaller.computeForecastAccuracy(request)).thenReturn(accuracy);
  }

  private void onPredictReturn(TimeSeriesAnalysisRequest request, TimeSeries timeSeries) {
    when(timeSeriesAnalysisEngineCaller.predict(request)).thenReturn(timeSeries);
  }

  private TimeSeriesAnalysisRequest buildTimeSeriesAnalysisRequest() {
    TimeSeriesRow row1 = new TimeSeriesRow("1960", 1.);
    TimeSeriesRow row2 = new TimeSeriesRow("1961", 2.);
    TimeSeriesRow row3 = new TimeSeriesRow("1962", 3.);

    List<TimeSeriesRow> rows = newArrayList(row1, row2, row3);
    TimeSeries timeSeries = new TimeSeries(rows, "Date", "Value", "%Y");

    return new TimeSeriesAnalysisRequest(timeSeries, 1);
  }

  private TimeSeries buildTimeSeries() {
    TimeSeriesRow row = new TimeSeriesRow("1961", 2.);

    List<TimeSeriesRow> rows = newArrayList(row);
    return new TimeSeries(rows, "Date", "Value", "%Y");
  }
}