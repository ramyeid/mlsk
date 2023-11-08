package org.mlsk.service.impl.classifier.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.service.model.classifier.ClassifierType;
import org.mlsk.service.engine.Engine;
import org.mlsk.service.impl.classifier.service.exception.ClassifierServiceException;
import org.mlsk.service.impl.orchestrator.Orchestrator;
import org.mlsk.service.impl.orchestrator.exception.NoBlockedEngineException;
import org.mlsk.service.model.classifier.*;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mlsk.service.impl.testhelper.OrchestratorHelper.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClassifierServiceImplTest {

  private static final long REQUEST_ID = 1L;
  private static final ClassifierType CLASSIFIER_TYPE = mock(ClassifierType.class);

  @Mock
  private Orchestrator orchestrator;
  @Mock
  private Engine engine;

  private ClassifierServiceImpl service;

  @BeforeEach
  public void setUp() {
    this.service = new ClassifierServiceImpl(orchestrator);
  }

  @Test
  public void should_delegate_call_to_orchestrator_and_engine_on_start() {
    ClassifierStartRequest request = buildClassifierStartRequest();
    onBookEngineReturn(orchestrator, engine, REQUEST_ID);
    onRunOnEngineCallMethod(orchestrator, engine, REQUEST_ID);
    when(CLASSIFIER_TYPE.getStartAction()).thenReturn("startAction");

    service.start(request);

    InOrder inOrder = buildInOrder();
    inOrder.verify(CLASSIFIER_TYPE).getStartAction();
    inOrder.verify(orchestrator).bookEngine(REQUEST_ID, "startAction");
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("startAction"), any());
    inOrder.verify(engine).start(buildClassifierStartRequest());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_result_on_start() {
    ClassifierStartRequest request = buildClassifierStartRequest();
    onBookEngineReturn(orchestrator, engine, REQUEST_ID);
    onRunOnEngineCallMethod(orchestrator, engine, REQUEST_ID);
    when(CLASSIFIER_TYPE.getStartAction()).thenReturn("startAction");

    ClassifierStartResponse actualResponse = service.start(request);

    assertEquals(buildExpectedClassifierStartResponse(), actualResponse);
  }

  @Test
  public void should_release_and_cancel_request_on_start_failure() {
    ClassifierStartRequest request = buildClassifierStartRequest();
    onBookEngineReturn(orchestrator, engine, REQUEST_ID);
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "startAction", "start exception message");
    when(CLASSIFIER_TYPE.getStartAction()).thenReturn("startAction");
    when(CLASSIFIER_TYPE.getCancelAction()).thenReturn("cancelAction");

    try {
      service.start(request);
      fail("should throw exception");

    } catch (Exception ignored) {
    }
    InOrder inOrder = buildInOrder();
    inOrder.verify(CLASSIFIER_TYPE).getStartAction();
    inOrder.verify(orchestrator).bookEngine(REQUEST_ID, "startAction");
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("startAction"), any());
    inOrder.verify(engine).start(buildClassifierStartRequest());
    inOrder.verify(CLASSIFIER_TYPE).getCancelAction();
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("cancelAction"), any());
    inOrder.verify(engine).cancel(buildClassifierCancelRequest());
    inOrder.verify(orchestrator).completeRequest(REQUEST_ID, "startAction");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_classifier_service_exception_on_start_failure() {
    ClassifierStartRequest request = buildClassifierStartRequest();
    onBookEngineReturn(orchestrator, engine, REQUEST_ID);
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "startAction", "start exception message");
    when(CLASSIFIER_TYPE.getStartAction()).thenReturn("startAction");
    when(CLASSIFIER_TYPE.getCancelAction()).thenReturn("cancelAction");

    try {
      service.start(request);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "start exception message");
    }
  }

  @Test
  public void should_ignore_if_release_throws_no_booked_engine_exception_on_start_failure() {
    ClassifierStartRequest request = buildClassifierStartRequest();
    onBookEngineReturn(orchestrator, engine, REQUEST_ID);
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "startAction", "start exception message");
    doThrowExceptionOnCompleteRequest(orchestrator, REQUEST_ID, "startAction", new NoBlockedEngineException("ignored exception"));
    when(CLASSIFIER_TYPE.getStartAction()).thenReturn("startAction");
    when(CLASSIFIER_TYPE.getCancelAction()).thenReturn("cancelAction");

    try {
      service.start(request);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "start exception message");
    }
  }

  @Test
  public void should_delegate_call_to_orchestrator_and_engine_on_data() {
    ClassifierDataRequest request = buildClassifierDataRequest();
    onRunOnEngineCallMethod(orchestrator, engine, REQUEST_ID);
    when(CLASSIFIER_TYPE.getDataAction()).thenReturn("dataAction");

    service.data(request);

    InOrder inOrder = buildInOrder();
    inOrder.verify(CLASSIFIER_TYPE).getDataAction();
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("dataAction"), any());
    inOrder.verify(engine).data(buildClassifierDataRequest());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_release_and_cancel_request_on_data_failure() {
    ClassifierDataRequest request = buildClassifierDataRequest();
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "dataAction", "data exception message");
    when(CLASSIFIER_TYPE.getDataAction()).thenReturn("dataAction");
    when(CLASSIFIER_TYPE.getCancelAction()).thenReturn("cancelAction");

    try {
      service.data(request);
      fail("should throw exception");

    } catch (Exception ignored) {
    }
    InOrder inOrder = buildInOrder();
    inOrder.verify(CLASSIFIER_TYPE).getDataAction();
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("dataAction"), any());
    inOrder.verify(engine).data(buildClassifierDataRequest());
    inOrder.verify(CLASSIFIER_TYPE).getCancelAction();
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("cancelAction"), any());
    inOrder.verify(engine).cancel(buildClassifierCancelRequest());
    inOrder.verify(orchestrator).completeRequest(REQUEST_ID, "dataAction");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_classifier_service_exception_on_data_failure() {
    ClassifierDataRequest request = buildClassifierDataRequest();
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "dataAction", "data exception message");
    when(CLASSIFIER_TYPE.getDataAction()).thenReturn("dataAction");
    when(CLASSIFIER_TYPE.getCancelAction()).thenReturn("cancelAction");

    try {
      service.data(request);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "data exception message");
    }
  }

  @Test
  public void should_ignore_if_release_throws_no_booked_engine_exception_on_data_failure() {
    ClassifierDataRequest request = buildClassifierDataRequest();
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "dataAction", "data exception message");
    doThrowExceptionOnCompleteRequest(orchestrator, REQUEST_ID, "dataAction", new NoBlockedEngineException("ignored exception"));
    when(CLASSIFIER_TYPE.getDataAction()).thenReturn("dataAction");
    when(CLASSIFIER_TYPE.getCancelAction()).thenReturn("cancelAction");

    try {
      service.data(request);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "data exception message");
    }
  }

  @Test
  public void should_delegate_call_to_orchestrator_and_engine_on_predict() {
    ClassifierRequest request = buildClassifierRequest();
    onRunOnEngineCallMethod(orchestrator, engine, REQUEST_ID);
    when(CLASSIFIER_TYPE.getPredictAction()).thenReturn("predictAction");

    service.predict(request);

    InOrder inOrder = buildInOrder();
    inOrder.verify(CLASSIFIER_TYPE).getPredictAction();
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("predictAction"), any());
    inOrder.verify(engine).predict(request);
    inOrder.verify(orchestrator).completeRequest(REQUEST_ID, "predictAction");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_result_on_predict() {
    ClassifierRequest request = buildClassifierRequest();
    onRunOnEngineCallMethod(orchestrator, engine, REQUEST_ID);
    onEnginePredictReturn(request, buildClassifierResponse());
    when(CLASSIFIER_TYPE.getPredictAction()).thenReturn("predictAction");

    ClassifierResponse actualResponse = service.predict(request);

    assertEquals(buildClassifierResponse(), actualResponse);
  }

  @Test
  public void should_release_and_cancel_request_on_predict_failure() {
    ClassifierRequest request = buildClassifierRequest();
    ClassifierCancelRequest cancelRequest = buildClassifierCancelRequest();
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "predictAction", "predict exception message");
    when(CLASSIFIER_TYPE.getPredictAction()).thenReturn("predictAction");
    when(CLASSIFIER_TYPE.getCancelAction()).thenReturn("cancelAction");

    try {
      service.predict(request);
      fail("should throw exception");

    } catch (Exception ignored) {
    }
    InOrder inOrder = buildInOrder();
    inOrder.verify(CLASSIFIER_TYPE).getPredictAction();
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("predictAction"), any());
    inOrder.verify(engine).predict(request);
    inOrder.verify(CLASSIFIER_TYPE).getCancelAction();
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("cancelAction"), any());
    inOrder.verify(engine).cancel(cancelRequest);
    inOrder.verify(orchestrator).completeRequest(REQUEST_ID, "predictAction");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_classifier_service_exception_on_predict_failure() {
    ClassifierRequest request = buildClassifierRequest();
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "predictAction", "predict exception message");
    when(CLASSIFIER_TYPE.getPredictAction()).thenReturn("predictAction");
    when(CLASSIFIER_TYPE.getCancelAction()).thenReturn("cancelAction");

    try {
      service.predict(request);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "predict exception message");
    }
  }

  @Test
  public void should_ignore_if_release_throws_no_booked_engine_exception_on_predict_failure() {
    ClassifierRequest request = buildClassifierRequest();
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "predictAction", "predict exception message");
    doThrowExceptionOnCompleteRequest(orchestrator, REQUEST_ID, "predictAction", new NoBlockedEngineException("ignored exception"));
    when(CLASSIFIER_TYPE.getPredictAction()).thenReturn("predictAction");
    when(CLASSIFIER_TYPE.getCancelAction()).thenReturn("cancelAction");

    try {
      service.predict(request);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "predict exception message");
    }
  }

  @Test
  public void should_delegate_call_to_orchestrator_and_engine_on_predict_accuracy() {
    ClassifierRequest request = buildClassifierRequest();
    onRunOnEngineCallMethod(orchestrator, engine, REQUEST_ID);
    when(CLASSIFIER_TYPE.getPredictAccuracyAction()).thenReturn("predictAccuracyAction");

    service.computePredictAccuracy(request);

    InOrder inOrder = buildInOrder();
    inOrder.verify(CLASSIFIER_TYPE).getPredictAccuracyAction();
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("predictAccuracyAction"), any());
    inOrder.verify(engine).computePredictAccuracy(request);
    inOrder.verify(orchestrator).completeRequest(REQUEST_ID, "predictAccuracyAction");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_result_on_predict_accuracy() {
    ClassifierRequest request = buildClassifierRequest();
    onRunOnEngineCallMethod(orchestrator, engine, REQUEST_ID);
    onEngineComputePredictAccuracyReturn(request, 45.1);
    when(CLASSIFIER_TYPE.getPredictAccuracyAction()).thenReturn("predictAccuracyAction");

    Double actualAccuracy = service.computePredictAccuracy(request);

    assertEquals(45.1, actualAccuracy);
  }

  @Test
  public void should_release_and_cancel_request_on_predict_accuracy_failure() {
    ClassifierRequest request = buildClassifierRequest();
    ClassifierCancelRequest classifierCancelRequest = buildClassifierCancelRequest();
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "predictAccuracyAction", "predict accuracy exception message");
    when(CLASSIFIER_TYPE.getPredictAccuracyAction()).thenReturn("predictAccuracyAction");
    when(CLASSIFIER_TYPE.getCancelAction()).thenReturn("cancelAction");

    try {
      service.computePredictAccuracy(request);
      fail("should throw exception");

    } catch (Exception ignored) {
    }
    InOrder inOrder = buildInOrder();
    inOrder.verify(CLASSIFIER_TYPE).getPredictAccuracyAction();
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("predictAccuracyAction"), any());
    inOrder.verify(engine).computePredictAccuracy(request);
    inOrder.verify(CLASSIFIER_TYPE).getCancelAction();
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("cancelAction"), any());
    inOrder.verify(engine).cancel(classifierCancelRequest);
    inOrder.verify(orchestrator).completeRequest(REQUEST_ID, "predictAccuracyAction");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_classifier_service_exception_on_predict_accuracy_failure() {
    ClassifierRequest request = buildClassifierRequest();
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "predictAccuracyAction", "predict accuracy exception message");
    when(CLASSIFIER_TYPE.getPredictAccuracyAction()).thenReturn("predictAccuracyAction");
    when(CLASSIFIER_TYPE.getCancelAction()).thenReturn("cancelAction");

    try {
      service.computePredictAccuracy(request);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "predict accuracy exception message");
    }
  }

  @Test
  public void should_ignore_if_release_throws_no_booked_engine_exception_on_predict_accuracy_failure() {
    ClassifierRequest request = buildClassifierRequest();
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "predictAccuracyAction", "predict accuracy exception message");
    doThrowExceptionOnCompleteRequest(orchestrator, REQUEST_ID, "predictAccuracyAction", new NoBlockedEngineException("ignored exception"));
    when(CLASSIFIER_TYPE.getPredictAccuracyAction()).thenReturn("predictAccuracyAction");
    when(CLASSIFIER_TYPE.getCancelAction()).thenReturn("cancelAction");

    try {
      service.computePredictAccuracy(request);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "predict accuracy exception message");
    }
  }

  private InOrder buildInOrder() {
    return inOrder(orchestrator, engine, CLASSIFIER_TYPE);
  }

  private void onEnginePredictReturn(ClassifierRequest classifierRequest, ClassifierResponse classifierResponse) {
    when(engine.predict(classifierRequest)).thenReturn(classifierResponse);
  }

  private void onEngineComputePredictAccuracyReturn(ClassifierRequest classifierRequest, Double accuracy) {
    when(engine.computePredictAccuracy(classifierRequest)).thenReturn(accuracy);
  }

  private static ClassifierStartRequest buildClassifierStartRequest() {
    return new ClassifierStartRequest(REQUEST_ID, "predictionColumnName", newArrayList("col1"), 3, CLASSIFIER_TYPE);
  }

  private static ClassifierStartResponse buildExpectedClassifierStartResponse() {
    return new ClassifierStartResponse(REQUEST_ID, CLASSIFIER_TYPE);
  }

  private static ClassifierDataRequest buildClassifierDataRequest() {
    return new ClassifierDataRequest(REQUEST_ID, "columnName", newArrayList(0, 1), CLASSIFIER_TYPE);
  }

  private static ClassifierRequest buildClassifierRequest() {
    return new ClassifierRequest(REQUEST_ID, CLASSIFIER_TYPE);
  }

  private static ClassifierResponse buildClassifierResponse() {
    return new ClassifierResponse(REQUEST_ID, "columnName", newArrayList(0, 1), CLASSIFIER_TYPE);
  }

  private static ClassifierCancelRequest buildClassifierCancelRequest() {
    return new ClassifierCancelRequest(REQUEST_ID, CLASSIFIER_TYPE);
  }

  private static void assertOnClassifierServiceException(Exception exception, String exceptionMessage) {
    assertInstanceOf(ClassifierServiceException.class, exception);
    assertEquals(exceptionMessage, exception.getMessage());
  }
}
