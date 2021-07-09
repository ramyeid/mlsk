package org.machinelearning.swissknife.service.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.machinelearning.swissknife.model.EngineState;
import org.machinelearning.swissknife.model.ServiceInformation;
import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesRow;
import org.machinelearning.swissknife.service.engine.client.EngineClientFactory;
import org.machinelearning.swissknife.service.engine.client.timeseries.TimeSeriesAnalysisEngineClient;
import org.machinelearning.swissknife.service.engine.process.ResilientProcess;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.machinelearning.swissknife.model.EngineState.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EngineImplTest {

  @Mock
  private EngineClientFactory engineClientFactory;
  @Mock
  private TimeSeriesAnalysisEngineClient timeSeriesAnalysisEngineClient;
  @Mock
  private AtomicReference<EngineState> engineState;

  private final ServiceInformation serviceInformation = new ServiceInformation("host", "port");
  private final TimeSeriesRow timeSeriesRow = new TimeSeriesRow("1960", 1.);
  private final TimeSeriesRow timeSeriesRow1 = new TimeSeriesRow("1961", 2.);
  private final TimeSeriesRow timeSeriesRow2 = new TimeSeriesRow("1962", 3.);
  private final TimeSeries timeSeries = new TimeSeries(asList(timeSeriesRow, timeSeriesRow1), "Date", "Value", "%Y");
  private final TimeSeries timeSeries2 = new TimeSeries(singletonList(timeSeriesRow2), "Date", "Value", "%Y");
  private final TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = new TimeSeriesAnalysisRequest(timeSeries, 1);
  private final Double accuracy = 33.;

  @BeforeEach
  public void setUp() {
  }

  @Test
  public void should_modify_state_and_relaunch_engine_when_killed() throws IOException, InterruptedException {
    ResilientProcess resilientProcess = mock(ResilientProcess.class);
    EngineImpl engineImpl = new EngineImpl(serviceInformation, engineClientFactory, engineState, resilientProcess);

    engineImpl.onProcessKilled();

    InOrder inOrder = inOrder(resilientProcess, engineState);
    inOrder.verify(engineState).set(OFF);
    inOrder.verify(resilientProcess).launchProcess();
    inOrder.verify(engineState).set(WAITING);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_keep_state_off_when_engine_fail_to_relaunch() throws IOException, InterruptedException {
    ResilientProcess resilientProcess = mock(ResilientProcess.class);
    doThrow(new RuntimeException("Exception while relaunch")).when(resilientProcess).launchProcess();
    EngineImpl engineImpl = new EngineImpl(serviceInformation, engineClientFactory, engineState, resilientProcess);

    engineImpl.onProcessKilled();

    InOrder inOrder = inOrder(resilientProcess, engineState);
    inOrder.verify(engineState).set(OFF);
    inOrder.verify(resilientProcess).launchProcess();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_delegate_forecast_call_to_engine() throws IOException, InterruptedException {
    when(engineClientFactory.buildTimeSeriesAnalysisEngineClient(any())).thenReturn(timeSeriesAnalysisEngineClient);
    when(timeSeriesAnalysisEngineClient.forecast(any())).thenReturn(timeSeries2);
    EngineImpl engineImpl = new EngineImpl(serviceInformation, engineClientFactory, new AtomicReference<>(WAITING), mock(ResilientProcess.class));

    TimeSeries actualTimeSeries = engineImpl.forecast(timeSeriesAnalysisRequest);

    verify(timeSeriesAnalysisEngineClient).forecast(any());
    assertEquals(timeSeries2, actualTimeSeries);
    assertEquals(WAITING, engineImpl.getState());
  }

  @Test
  public void should_modify_state_to_computing_and_then_waiting_on_forecast() throws IOException, InterruptedException {
    when(engineClientFactory.buildTimeSeriesAnalysisEngineClient(any())).thenReturn(timeSeriesAnalysisEngineClient);
    when(timeSeriesAnalysisEngineClient.forecast(any())).thenReturn(timeSeries2);
    EngineImpl engineImpl = new EngineImpl(serviceInformation, engineClientFactory, engineState, mock(ResilientProcess.class));

    engineImpl.forecast(timeSeriesAnalysisRequest);

    InOrder inOrder = inOrder(engineState);
    inOrder.verify(engineState).set(COMPUTING);
    inOrder.verify(engineState).set(WAITING);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_call_forecast_with_last_number_of_values_elements_removed_on_forecast_vs_actual() throws IOException, InterruptedException {
    when(engineClientFactory.buildTimeSeriesAnalysisEngineClient(any())).thenReturn(timeSeriesAnalysisEngineClient);
    when(timeSeriesAnalysisEngineClient.forecast(any())).thenReturn(timeSeries2);
    EngineImpl engineImpl = new EngineImpl(serviceInformation, engineClientFactory, new AtomicReference<>(WAITING), mock(ResilientProcess.class));

    TimeSeries actualTimeSeries = engineImpl.forecastVsActual(timeSeriesAnalysisRequest);

    TimeSeries timeSeriesSentToEngine = new TimeSeries(singletonList(timeSeriesRow), "Date", "Value", "%Y");
    TimeSeriesAnalysisRequest timeSeriesAnalysisRequestSentToEngine = new TimeSeriesAnalysisRequest(timeSeriesSentToEngine, 1);
    verify(timeSeriesAnalysisEngineClient).forecast(timeSeriesAnalysisRequestSentToEngine);
    assertEquals(timeSeries2, actualTimeSeries);
    assertEquals(WAITING, engineImpl.getState());
  }

  @Test
  public void should_modify_state_to_computing_and_then_waiting_on_forecast_vs_actual() throws IOException, InterruptedException {
    when(engineClientFactory.buildTimeSeriesAnalysisEngineClient(any())).thenReturn(timeSeriesAnalysisEngineClient);
    when(timeSeriesAnalysisEngineClient.forecast(any())).thenReturn(timeSeries2);
    EngineImpl engineImpl = new EngineImpl(serviceInformation, engineClientFactory, engineState, mock(ResilientProcess.class));

    engineImpl.forecastVsActual(timeSeriesAnalysisRequest);

    InOrder inOrder = inOrder(engineState);
    inOrder.verify(engineState).set(COMPUTING);
    inOrder.verify(engineState).set(WAITING);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_delegate_compute_forecast_accuracy_to_engine() throws IOException, InterruptedException {
    when(engineClientFactory.buildTimeSeriesAnalysisEngineClient(any())).thenReturn(timeSeriesAnalysisEngineClient);
    when(timeSeriesAnalysisEngineClient.computeForecastAccuracy(any())).thenReturn(accuracy);
    EngineImpl engineImpl = new EngineImpl(serviceInformation, engineClientFactory, new AtomicReference<>(WAITING), mock(ResilientProcess.class));

    Double actualAccuracy = engineImpl.computeForecastAccuracy(new TimeSeriesAnalysisRequest(timeSeries, 1));

    verify(timeSeriesAnalysisEngineClient).computeForecastAccuracy(any());
    assertEquals(accuracy, actualAccuracy);
    assertEquals(WAITING, engineImpl.getState());
  }

  @Test
  public void should_modify_state_to_computing_and_then_waiting_on_compute_forecast_accuracy() throws IOException, InterruptedException {
    when(engineClientFactory.buildTimeSeriesAnalysisEngineClient(any())).thenReturn(timeSeriesAnalysisEngineClient);
    when(timeSeriesAnalysisEngineClient.computeForecastAccuracy(any())).thenReturn(accuracy);
    EngineImpl engineImpl = new EngineImpl(serviceInformation, engineClientFactory, engineState, mock(ResilientProcess.class));

    engineImpl.computeForecastAccuracy(timeSeriesAnalysisRequest);

    InOrder inOrder = inOrder(engineState);
    inOrder.verify(engineState).set(COMPUTING);
    inOrder.verify(engineState).set(WAITING);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_delegate_predict_call_to_engine() throws IOException, InterruptedException {
    when(engineClientFactory.buildTimeSeriesAnalysisEngineClient(any())).thenReturn(timeSeriesAnalysisEngineClient);
    when(timeSeriesAnalysisEngineClient.predict(any())).thenReturn(timeSeries2);
    EngineImpl engineImpl = new EngineImpl(serviceInformation, engineClientFactory, new AtomicReference<>(WAITING), mock(ResilientProcess.class));

    TimeSeries actualTimeSeries = engineImpl.predict(new TimeSeriesAnalysisRequest(timeSeries, 1));

    verify(timeSeriesAnalysisEngineClient).predict(any());
    assertEquals(timeSeries2, actualTimeSeries);
    assertEquals(WAITING, engineImpl.getState());
  }

  @Test
  public void should_modify_state_to_computing_and_then_waiting_on_predict() throws IOException, InterruptedException {
    when(engineClientFactory.buildTimeSeriesAnalysisEngineClient(any())).thenReturn(timeSeriesAnalysisEngineClient);
    when(timeSeriesAnalysisEngineClient.predict(any())).thenReturn(timeSeries2);
    EngineImpl engineImpl = new EngineImpl(serviceInformation, engineClientFactory, engineState, mock(ResilientProcess.class));

    engineImpl.predict(timeSeriesAnalysisRequest);

    InOrder inOrder = inOrder(engineState);
    inOrder.verify(engineState).set(COMPUTING);
    inOrder.verify(engineState).set(WAITING);
    inOrder.verifyNoMoreInteractions();
  }

}