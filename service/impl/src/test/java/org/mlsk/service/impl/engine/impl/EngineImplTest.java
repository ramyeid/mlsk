package org.mlsk.service.impl.engine.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.lib.engine.ResilientEngineProcess;
import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.service.impl.engine.client.EngineClientFactory;
import org.mlsk.service.impl.engine.impl.exception.UnableToLaunchEngineException;
import org.mlsk.service.impl.timeseries.engine.TimeSeriesAnalysisEngineClient;
import org.mlsk.service.model.engine.EngineState;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.model.timeseries.TimeSeriesRow;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mlsk.service.model.engine.EngineState.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EngineImplTest {

  private static final ServiceInformation SERVICE_INFORMATION = new ServiceInformation("host",  1231L);

  @Mock
  private ResilientEngineProcess resilientEngineProcess;
  @Mock
  private EngineClientFactory engineClientFactory;
  @Mock
  private TimeSeriesAnalysisEngineClient tsaEngineClient;

  private AtomicReference<EngineState> engineStateSpy;
  private EngineImpl engineImpl;

  @BeforeEach
  public void setUp() {
    engineStateSpy = spy(new AtomicReference<>(OFF));
    engineImpl = new EngineImpl(engineClientFactory, SERVICE_INFORMATION, resilientEngineProcess, engineStateSpy);
  }

  @Test
  public void should_modify_state_and_launch_engine_when_engine_off() throws Exception {

    engineImpl.launchEngine();

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineStateSpy).get();
    inOrder.verify(resilientEngineProcess).launchEngine(any());
    inOrder.verify(engineStateSpy).set(WAITING);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_modify_state_and_launch_engine_when_engine_is_down() throws Exception {
    engineStateSpy.set(WAITING);
    when(resilientEngineProcess.isEngineUp()).thenReturn(false);

    engineImpl.launchEngine();

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineStateSpy).get();
    inOrder.verify(resilientEngineProcess).isEngineUp();
    inOrder.verify(resilientEngineProcess).launchEngine(any());
    inOrder.verify(engineStateSpy).set(WAITING);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_do_nothing_if_engine_is_up_and_waiting() {
    engineStateSpy.set(WAITING);
    when(resilientEngineProcess.isEngineUp()).thenReturn(true);

    engineImpl.launchEngine();

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineStateSpy).get();
    inOrder.verify(resilientEngineProcess).isEngineUp();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_exception_and_set_status_to_off_if_unable_to_launch_engine() throws Exception {
    throwExceptionOnLaunchEngine();

    try {
      engineImpl.launchEngine();

    } catch (Exception exception) {
      assertInstanceOf(UnableToLaunchEngineException.class, exception);
      assertEquals("Unable to launch engine ServiceInformation{host='host', port='1231'}", exception.getMessage());
      assertEquals(OFF, engineStateSpy.get());
    }
  }

  @Test
  public void should_modify_state_and_relaunch_engine_when_killed() throws Exception {
    engineStateSpy.set(WAITING);

    engineImpl.onEngineKilled();

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineStateSpy).set(OFF);
    inOrder.verify(engineStateSpy).get();
    inOrder.verify(resilientEngineProcess).launchEngine(any());
    inOrder.verify(engineStateSpy).set(WAITING);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_keep_state_off_when_engine_fail_to_relaunch() throws Exception {
    engineStateSpy.set(WAITING);
    throwExceptionOnLaunchEngine();

    engineImpl.onEngineKilled();

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineStateSpy).set(OFF);
    inOrder.verify(engineStateSpy).get();
    inOrder.verify(resilientEngineProcess).launchEngine(any());
    inOrder.verify(engineStateSpy).set(OFF);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_delegate_forecast_call_to_engine() {
    onBuildTimeSeriesAnalysisEngineClient();
    onForecastReturn(buildTimeSeriesAnalysisRequest(), buildTimeSeries());

    engineImpl.forecast(buildTimeSeriesAnalysisRequest());

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineClientFactory).buildTimeSeriesAnalysisEngineClient(SERVICE_INFORMATION);
    inOrder.verify(engineStateSpy).set(COMPUTING);
    inOrder.verify(tsaEngineClient).forecast(buildTimeSeriesAnalysisRequest());
    inOrder.verify(engineStateSpy).set(WAITING);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_time_series_and_reset_state_on_forecast() {
    onBuildTimeSeriesAnalysisEngineClient();
    onForecastReturn(buildTimeSeriesAnalysisRequest(), buildTimeSeries());

    TimeSeries actualTimeSeries = engineImpl.forecast(buildTimeSeriesAnalysisRequest());

    assertEquals(buildTimeSeries(), actualTimeSeries);
    assertEquals(WAITING, engineImpl.getState());
  }

  @Test
  public void should_delegate_compute_forecast_accuracy_call_to_engine() {
    onBuildTimeSeriesAnalysisEngineClient();
    onComputeForecastAccuracyReturn(buildTimeSeriesAnalysisRequest(), 123.1);

    engineImpl.computeForecastAccuracy(buildTimeSeriesAnalysisRequest());

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineClientFactory).buildTimeSeriesAnalysisEngineClient(SERVICE_INFORMATION);
    inOrder.verify(engineStateSpy).set(COMPUTING);
    inOrder.verify(tsaEngineClient).computeForecastAccuracy(buildTimeSeriesAnalysisRequest());
    inOrder.verify(engineStateSpy).set(WAITING);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_accuracy_and_reset_state_on_compute_forecast_accuracy() {
    onBuildTimeSeriesAnalysisEngineClient();
    onComputeForecastAccuracyReturn(buildTimeSeriesAnalysisRequest(), 3.0);

    Double actualAccuracy = engineImpl.computeForecastAccuracy(buildTimeSeriesAnalysisRequest());

    assertEquals(3.0, actualAccuracy);
    assertEquals(WAITING, engineImpl.getState());
  }

  @Test
  public void should_delegate_predict_call_to_engine() {
    onBuildTimeSeriesAnalysisEngineClient();
    onPredictReturn(buildTimeSeriesAnalysisRequest(), buildTimeSeries());

    engineImpl.predict(buildTimeSeriesAnalysisRequest());

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineClientFactory).buildTimeSeriesAnalysisEngineClient(SERVICE_INFORMATION);
    inOrder.verify(engineStateSpy).set(COMPUTING);
    inOrder.verify(tsaEngineClient).predict(buildTimeSeriesAnalysisRequest());
    inOrder.verify(engineStateSpy).set(WAITING);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_time_series_and_reset_state_on_predict() {
    onBuildTimeSeriesAnalysisEngineClient();
    onPredictReturn(buildTimeSeriesAnalysisRequest(), buildTimeSeries());

    TimeSeries actualTimeSeries = engineImpl.predict(buildTimeSeriesAnalysisRequest());

    assertEquals(buildTimeSeries(), actualTimeSeries);
    assertEquals(WAITING, engineImpl.getState());
  }

  private InOrder buildInOrder() {
    return inOrder(engineClientFactory, tsaEngineClient, engineStateSpy, resilientEngineProcess);
  }

  private void onBuildTimeSeriesAnalysisEngineClient() {
    when(engineClientFactory.buildTimeSeriesAnalysisEngineClient(any())).thenReturn(tsaEngineClient);
  }

  private void throwExceptionOnLaunchEngine() throws Exception {
    doThrow(new RuntimeException("Exception while relaunch")).when(resilientEngineProcess).launchEngine(any());
  }

  private void onForecastReturn(TimeSeriesAnalysisRequest request, TimeSeries timeSeries) {
    when(tsaEngineClient.forecast(request)).thenReturn(timeSeries);
  }

  private void onComputeForecastAccuracyReturn(TimeSeriesAnalysisRequest request, Double accuracy) {
    when(tsaEngineClient.computeForecastAccuracy(request)).thenReturn(accuracy);
  }

  private void onPredictReturn(TimeSeriesAnalysisRequest request, TimeSeries timeSeries) {
    when(tsaEngineClient.predict(request)).thenReturn(timeSeries);
  }

  private static TimeSeriesAnalysisRequest buildTimeSeriesAnalysisRequest() {
    TimeSeriesRow row1 = new TimeSeriesRow("1960", 1.);
    TimeSeriesRow row2 = new TimeSeriesRow("1961", 2.);
    TimeSeriesRow row3 = new TimeSeriesRow("1962", 3.);

    List<TimeSeriesRow> rows = newArrayList(row1, row2, row3);
    TimeSeries timeSeries = new TimeSeries(rows, "Date", "Value", "%Y");

    return new TimeSeriesAnalysisRequest(timeSeries, 1);
  }

  private static TimeSeries buildTimeSeries() {
    TimeSeriesRow row = new TimeSeriesRow("1961", 2.);

    List<TimeSeriesRow> rows = newArrayList(row);
    return new TimeSeries(rows, "Date", "Value", "%Y");
  }
}