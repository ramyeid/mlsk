package org.mlsk.service.impl.engine.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.lib.engine.ResilientEngineProcess;
import org.mlsk.lib.model.Endpoint;
import org.mlsk.service.classifier.ClassifierType;
import org.mlsk.service.impl.classifier.engine.ClassifierEngineClient;
import org.mlsk.service.impl.engine.client.EngineClientFactory;
import org.mlsk.service.impl.engine.impl.exception.UnableToLaunchEngineException;
import org.mlsk.service.impl.timeseries.engine.TimeSeriesAnalysisEngineClient;
import org.mlsk.service.model.classifier.ClassifierDataRequest;
import org.mlsk.service.model.classifier.ClassifierDataResponse;
import org.mlsk.service.model.classifier.ClassifierStartRequest;
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

  private static final Endpoint ENDPOINT = new Endpoint("host", 1231L);

  @Mock
  private ResilientEngineProcess resilientEngineProcess;
  @Mock
  private EngineClientFactory engineClientFactory;
  @Mock
  private TimeSeriesAnalysisEngineClient tsaEngineClient;
  @Mock
  private ClassifierEngineClient classifierEngineClient;

  private AtomicReference<EngineState> engineStateSpy;
  private EngineImpl engineImpl;

  @BeforeEach
  public void setUp() {
    engineStateSpy = spy(new AtomicReference<>(OFF));
    engineImpl = new EngineImpl(engineClientFactory, ENDPOINT, resilientEngineProcess, engineStateSpy);
  }

  @Test
  public void should_return_endpoint() {

    Endpoint actualInfo = engineImpl.getEndpoint();

    assertEquals(ENDPOINT, actualInfo);
  }

  @Test
  public void should_start_with_off_state() {

    EngineState actualState = engineImpl.getState();

    assertEquals(OFF, actualState);
  }

  @Test
  public void should_set_state_as_waiting() {

    engineImpl.markAsWaitingForRequest();

    assertEquals(WAITING, engineImpl.getState());
    assertEquals(WAITING, engineStateSpy.get());
  }

  @Test
  public void should_set_state_as_booked() {

    engineImpl.bookEngine();

    assertEquals(BOOKED, engineImpl.getState());
    assertEquals(BOOKED, engineStateSpy.get());
  }

  @Test
  public void should_set_state_as_computing() {

    engineImpl.markAsComputing();

    assertEquals(COMPUTING, engineImpl.getState());
    assertEquals(COMPUTING, engineStateSpy.get());
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
      assertEquals("Unable to launch engine Endpoint{host='host', port='1231'}", exception.getMessage());
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
    inOrder.verify(engineClientFactory).buildTimeSeriesAnalysisEngineClient(ENDPOINT);
    inOrder.verify(tsaEngineClient).forecast(buildTimeSeriesAnalysisRequest());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_time_series_on_forecast() {
    onBuildTimeSeriesAnalysisEngineClient();
    onForecastReturn(buildTimeSeriesAnalysisRequest(), buildTimeSeries());

    TimeSeries actualTimeSeries = engineImpl.forecast(buildTimeSeriesAnalysisRequest());

    assertEquals(buildTimeSeries(), actualTimeSeries);
  }

  @Test
  public void should_delegate_compute_forecast_accuracy_call_to_engine() {
    onBuildTimeSeriesAnalysisEngineClient();
    onComputeForecastAccuracyReturn(buildTimeSeriesAnalysisRequest(), 123.1);

    engineImpl.computeForecastAccuracy(buildTimeSeriesAnalysisRequest());

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineClientFactory).buildTimeSeriesAnalysisEngineClient(ENDPOINT);
    inOrder.verify(tsaEngineClient).computeForecastAccuracy(buildTimeSeriesAnalysisRequest());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_accuracy_on_compute_forecast_accuracy() {
    onBuildTimeSeriesAnalysisEngineClient();
    onComputeForecastAccuracyReturn(buildTimeSeriesAnalysisRequest(), 3.0);

    Double actualAccuracy = engineImpl.computeForecastAccuracy(buildTimeSeriesAnalysisRequest());

    assertEquals(3.0, actualAccuracy);
  }

  @Test
  public void should_delegate_predict_call_to_engine() {
    onBuildTimeSeriesAnalysisEngineClient();
    onPredictReturn(buildTimeSeriesAnalysisRequest(), buildTimeSeries());

    engineImpl.predict(buildTimeSeriesAnalysisRequest());

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineClientFactory).buildTimeSeriesAnalysisEngineClient(ENDPOINT);
    inOrder.verify(tsaEngineClient).predict(buildTimeSeriesAnalysisRequest());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_time_series_on_predict() {
    onBuildTimeSeriesAnalysisEngineClient();
    onPredictReturn(buildTimeSeriesAnalysisRequest(), buildTimeSeries());

    TimeSeries actualTimeSeries = engineImpl.predict(buildTimeSeriesAnalysisRequest());

    assertEquals(buildTimeSeries(), actualTimeSeries);
  }

  @Test
  public void should_delegate_classifier_start_call_to_engine() {
    ClassifierType classifierType = mock(ClassifierType.class);
    onBuildClassifierEngineClient();

    engineImpl.start(buildClassifierStartRequest(), classifierType);

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineClientFactory).buildClassifierEngineClient(ENDPOINT);
    inOrder.verify(classifierEngineClient).start(buildClassifierStartRequest(), classifierType);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_delegate_classifier_data_call_to_engine() {
    ClassifierType classifierType = mock(ClassifierType.class);
    onBuildClassifierEngineClient();

    engineImpl.data(buildClassifierDataRequest(), classifierType);

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineClientFactory).buildClassifierEngineClient(ENDPOINT);
    inOrder.verify(classifierEngineClient).data(buildClassifierDataRequest(), classifierType);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_delegate_classifier_predict_call_to_engine() {
    ClassifierType classifierType = mock(ClassifierType.class);
    onBuildClassifierEngineClient();

    engineImpl.predict(classifierType);

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineClientFactory).buildClassifierEngineClient(ENDPOINT);
    inOrder.verify(classifierEngineClient).predict(classifierType);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_classifier_data_response_on_classifier_predict() {
    ClassifierType classifierType = mock(ClassifierType.class);
    onBuildClassifierEngineClient();
    onClassifierPredictReturn(buildClassifierDataResponse());

    ClassifierDataResponse actualDataResponse = engineImpl.predict(classifierType);

    assertEquals(buildClassifierDataResponse(), actualDataResponse);
  }

  @Test
  public void should_delegate_classifier_predict_accuracy_call_to_engine() {
    ClassifierType classifierType = mock(ClassifierType.class);
    onBuildClassifierEngineClient();

    engineImpl.computePredictAccuracy(classifierType);

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineClientFactory).buildClassifierEngineClient(ENDPOINT);
    inOrder.verify(classifierEngineClient).computePredictAccuracy(classifierType);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_classifier_data_response_on_classifier_predict_accuracy() {
    ClassifierType classifierType = mock(ClassifierType.class);
    onBuildClassifierEngineClient();
    onClassifierPredictAccuracyReturn(123.123);

    Double actualAccuracy = engineImpl.computePredictAccuracy(classifierType);

    assertEquals(123.123, actualAccuracy);
  }

  @Test
  public void should_delegate_classifier_cancel_call_to_engine() {
    ClassifierType classifierType = mock(ClassifierType.class);
    onBuildClassifierEngineClient();

    engineImpl.cancel(classifierType);

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineClientFactory).buildClassifierEngineClient(ENDPOINT);
    inOrder.verify(classifierEngineClient).cancel(classifierType);
    inOrder.verifyNoMoreInteractions();
  }

  private InOrder buildInOrder() {
    return inOrder(engineClientFactory, tsaEngineClient, classifierEngineClient, engineStateSpy, resilientEngineProcess);
  }

  private void onBuildClassifierEngineClient() {
    when(engineClientFactory.buildClassifierEngineClient(any())).thenReturn(classifierEngineClient);
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

  private void onClassifierPredictReturn(ClassifierDataResponse classifierDataResponse) {
    when(classifierEngineClient.predict(any())).thenReturn(classifierDataResponse);
  }

  private void onClassifierPredictAccuracyReturn(double accuracy) {
    when(classifierEngineClient.computePredictAccuracy(any())).thenReturn(accuracy);
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

  private static ClassifierStartRequest buildClassifierStartRequest() {
    return new ClassifierStartRequest("predictionColumnName", newArrayList("col0", "col1"), 2);
  }

  private static ClassifierDataRequest buildClassifierDataRequest() {
    return new ClassifierDataRequest("requestId", "columnName", newArrayList(0, 1, 0));
  }

  private static ClassifierDataResponse buildClassifierDataResponse() {
    return new ClassifierDataResponse("columnName", newArrayList(0, 1, 0));
  }
}