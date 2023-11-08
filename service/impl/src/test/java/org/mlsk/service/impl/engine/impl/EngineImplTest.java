package org.mlsk.service.impl.engine.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.engine.classifier.client.ClassifierEngineApi;
import org.mlsk.api.engine.classifier.model.*;
import org.mlsk.api.engine.timeseries.client.TimeSeriesAnalysisEngineApi;
import org.mlsk.api.engine.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.engine.timeseries.model.TimeSeriesModel;
import org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel;
import org.mlsk.lib.engine.ResilientEngineProcess;
import org.mlsk.lib.model.Endpoint;
import org.mlsk.service.impl.engine.client.EngineClientFactory;
import org.mlsk.service.impl.engine.impl.exception.UnableToLaunchEngineException;
import org.mlsk.service.model.classifier.*;
import org.mlsk.service.model.engine.EngineState;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.model.timeseries.TimeSeriesRow;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
  private static final long REQUEST_ID = 1L;

  @Mock
  private ResilientEngineProcess resilientEngineProcess;
  @Mock
  private EngineClientFactory engineClientFactory;
  @Mock
  private TimeSeriesAnalysisEngineApi tsaEngineClient;
  @Mock
  private ClassifierEngineApi classifierEngineApi;

  private AtomicReference<EngineState> engineStateSpy;
  private EngineImpl engineImpl;

  @BeforeEach
  public void setUp() {
    engineStateSpy = spy(new AtomicReference<>(OFF));
    onBuildClassifierEngineClient();
    onBuildTimeSeriesAnalysisEngineClient();
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
    onForecastReturn(buildTimeSeriesAnalysisRequestModel(), buildTimeSeriesModel());

    engineImpl.forecast(buildTimeSeriesAnalysisRequest());

    InOrder inOrder = buildInOrder();
    inOrder.verify(tsaEngineClient).forecast(buildTimeSeriesAnalysisRequestModel());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_time_series_on_forecast() {
    onForecastReturn(buildTimeSeriesAnalysisRequestModel(), buildTimeSeriesModel());

    TimeSeries actualTimeSeries = engineImpl.forecast(buildTimeSeriesAnalysisRequest());

    assertEquals(buildTimeSeries(), actualTimeSeries);
  }

  @Test
  public void should_delegate_compute_forecast_accuracy_call_to_engine() {
    onComputeForecastAccuracyReturn(buildTimeSeriesAnalysisRequestModel(), 123.1);

    engineImpl.computeForecastAccuracy(buildTimeSeriesAnalysisRequest());

    InOrder inOrder = buildInOrder();
    inOrder.verify(tsaEngineClient).computeAccuracyOfForecast(buildTimeSeriesAnalysisRequestModel());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_accuracy_on_compute_forecast_accuracy() {
    onComputeForecastAccuracyReturn(buildTimeSeriesAnalysisRequestModel(), 3.0);

    Double actualAccuracy = engineImpl.computeForecastAccuracy(buildTimeSeriesAnalysisRequest());

    assertEquals(3.0, actualAccuracy);
  }

  @Test
  public void should_delegate_predict_call_to_engine() {
    onPredictReturn(buildTimeSeriesAnalysisRequestModel(), buildTimeSeriesModel());

    engineImpl.predict(buildTimeSeriesAnalysisRequest());

    InOrder inOrder = buildInOrder();
    inOrder.verify(tsaEngineClient).predict(buildTimeSeriesAnalysisRequestModel());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_time_series_on_predict() {
    onPredictReturn(buildTimeSeriesAnalysisRequestModel(), buildTimeSeriesModel());

    TimeSeries actualTimeSeries = engineImpl.predict(buildTimeSeriesAnalysisRequest());

    assertEquals(buildTimeSeries(), actualTimeSeries);
  }

  @Test
  public void should_delegate_classifier_start_call_to_engine() {

    engineImpl.start(buildClassifierStartRequest());

    InOrder inOrder = buildInOrder();
    inOrder.verify(classifierEngineApi).start(buildClassifierStartRequestModel());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_delegate_classifier_data_call_to_engine() {

    engineImpl.data(buildClassifierDataRequest());

    InOrder inOrder = buildInOrder();
    inOrder.verify(classifierEngineApi).data(buildClassifierDataRequestModel());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_delegate_classifier_predict_call_to_engine() {
    ClassifierRequest classifierRequest = buildClassifierRequest();
    onClassifierPredictReturn(buildClassifierResponseModel());

    engineImpl.predict(classifierRequest);

    InOrder inOrder = buildInOrder();
    inOrder.verify(classifierEngineApi).predict(buildClassifierRequestModel());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_classifier_response_on_classifier_predict() {
    ClassifierRequest classifierRequest = buildClassifierRequest();
    onClassifierPredictReturn(buildClassifierResponseModel());

    ClassifierResponse actualResponse = engineImpl.predict(classifierRequest);

    assertEquals(buildClassifierResponse(), actualResponse);
  }

  @Test
  public void should_delegate_classifier_predict_accuracy_call_to_engine() {
    ClassifierRequest classifierRequest = buildClassifierRequest();
    onClassifierPredictAccuracyReturn(12.);

    engineImpl.computePredictAccuracy(classifierRequest);

    InOrder inOrder = buildInOrder();
    inOrder.verify(classifierEngineApi).computePredictAccuracy(buildClassifierRequestModel());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_classifier_response_on_classifier_predict_accuracy() {
    ClassifierRequest classifierRequest = buildClassifierRequest();
    onClassifierPredictAccuracyReturn(123.123);

    Double actualAccuracy = engineImpl.computePredictAccuracy(classifierRequest);

    assertEquals(123.123, actualAccuracy);
  }

  @Test
  public void should_delegate_classifier_cancel_call_to_engine() {
    ClassifierCancelRequest classifierCancelRequest = buildClassifierCancelRequest();

    engineImpl.cancel(classifierCancelRequest);

    InOrder inOrder = buildInOrder();
    inOrder.verify(classifierEngineApi).cancel(buildClassifierCancelRequestModel());
    inOrder.verifyNoMoreInteractions();
  }

  private InOrder buildInOrder() {
    return inOrder(engineClientFactory, tsaEngineClient, classifierEngineApi, engineStateSpy, resilientEngineProcess);
  }

  private void onBuildClassifierEngineClient() {
    when(engineClientFactory.buildClassifierClient(any())).thenReturn(classifierEngineApi);
  }

  private void onBuildTimeSeriesAnalysisEngineClient() {
    when(engineClientFactory.buildTimeSeriesAnalysisClient(any())).thenReturn(tsaEngineClient);
  }

  private void throwExceptionOnLaunchEngine() throws Exception {
    doThrow(new RuntimeException("Exception while relaunch")).when(resilientEngineProcess).launchEngine(any());
  }

  private void onForecastReturn(TimeSeriesAnalysisRequestModel request, TimeSeriesModel timeSeries) {
    when(tsaEngineClient.forecast(request)).thenReturn(timeSeries);
  }

  private void onComputeForecastAccuracyReturn(TimeSeriesAnalysisRequestModel request, double accuracy) {
    when(tsaEngineClient.computeAccuracyOfForecast(request)).thenReturn(BigDecimal.valueOf(accuracy));
  }

  private void onPredictReturn(TimeSeriesAnalysisRequestModel request, TimeSeriesModel timeSeries) {
    when(tsaEngineClient.predict(request)).thenReturn(timeSeries);
  }

  private void onClassifierPredictReturn(ClassifierResponseModel classifierResponse) {
    when(classifierEngineApi.predict(any())).thenReturn(classifierResponse);
  }

  private void onClassifierPredictAccuracyReturn(double accuracy) {
    when(classifierEngineApi.computePredictAccuracy(any())).thenReturn(BigDecimal.valueOf(accuracy));
  }

  private static TimeSeriesAnalysisRequest buildTimeSeriesAnalysisRequest() {
    TimeSeriesRow row1 = new TimeSeriesRow("1960", 1.);
    TimeSeriesRow row2 = new TimeSeriesRow("1961", 2.);
    TimeSeriesRow row3 = new TimeSeriesRow("1962", 3.);

    List<TimeSeriesRow> rows = newArrayList(row1, row2, row3);
    TimeSeries timeSeries = new TimeSeries(rows, "Date", "Value", "%Y");

    return new TimeSeriesAnalysisRequest(REQUEST_ID, timeSeries, 1);
  }

  private static TimeSeriesAnalysisRequestModel buildTimeSeriesAnalysisRequestModel() {
    TimeSeriesRowModel row1 = new TimeSeriesRowModel("1960", BigDecimal.valueOf(1.));
    TimeSeriesRowModel row2 = new TimeSeriesRowModel("1961", BigDecimal.valueOf(2.));
    TimeSeriesRowModel row3 = new TimeSeriesRowModel("1962", BigDecimal.valueOf(3.));

    List<TimeSeriesRowModel> rows = newArrayList(row1, row2, row3);
    TimeSeriesModel timeSeries = new TimeSeriesModel(rows, "Date", "Value", "%Y");

    return new TimeSeriesAnalysisRequestModel(REQUEST_ID, timeSeries, 1);
  }

  private static TimeSeries buildTimeSeries() {
    TimeSeriesRow row = new TimeSeriesRow("1961", 2.);

    List<TimeSeriesRow> rows = newArrayList(row);
    return new TimeSeries(rows, "Date", "Value", "%Y");
  }

  private static TimeSeriesModel buildTimeSeriesModel() {
    TimeSeriesRowModel row = new TimeSeriesRowModel("1961", BigDecimal.valueOf(2.));

    List<TimeSeriesRowModel> rows = newArrayList(row);
    return new TimeSeriesModel(rows, "Date", "Value", "%Y");
  }

  private static ClassifierStartRequest buildClassifierStartRequest() {
    return new ClassifierStartRequest(REQUEST_ID, "predictionColumnName", newArrayList("col0", "col1"), 2, ClassifierType.DECISION_TREE);
  }

  private static ClassifierStartRequestModel buildClassifierStartRequestModel() {
    return new ClassifierStartRequestModel(REQUEST_ID, "predictionColumnName", newArrayList("col0", "col1"), 2, ClassifierTypeModel.DECISION_TREE);
  }

  private static ClassifierDataRequest buildClassifierDataRequest() {
    return new ClassifierDataRequest(REQUEST_ID, "columnName", newArrayList(0, 1, 0), ClassifierType.DECISION_TREE);
  }

  private static ClassifierDataRequestModel buildClassifierDataRequestModel() {
    return new ClassifierDataRequestModel(REQUEST_ID, "columnName", newArrayList(0, 1, 0), ClassifierTypeModel.DECISION_TREE);
  }

  private static ClassifierRequest buildClassifierRequest() {
    return new ClassifierRequest(REQUEST_ID, ClassifierType.DECISION_TREE);
  }

  private static ClassifierRequestModel buildClassifierRequestModel() {
    return new ClassifierRequestModel(REQUEST_ID, ClassifierTypeModel.DECISION_TREE);
  }

  private static ClassifierCancelRequest buildClassifierCancelRequest() {
    return new ClassifierCancelRequest(REQUEST_ID, ClassifierType.DECISION_TREE);
  }

  private static ClassifierCancelRequestModel buildClassifierCancelRequestModel() {
    return new ClassifierCancelRequestModel(REQUEST_ID, ClassifierTypeModel.DECISION_TREE);
  }

  private static ClassifierResponse buildClassifierResponse() {
    return new ClassifierResponse(REQUEST_ID, "columnName", newArrayList(0, 1, 0), ClassifierType.DECISION_TREE);
  }

  private static ClassifierResponseModel buildClassifierResponseModel() {
    return new ClassifierResponseModel(REQUEST_ID, "columnName", newArrayList(0, 1, 0), ClassifierTypeModel.DECISION_TREE);
  }
}