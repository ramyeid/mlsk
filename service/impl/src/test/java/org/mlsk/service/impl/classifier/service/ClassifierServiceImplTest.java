package org.mlsk.service.impl.classifier.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.service.classifier.ClassifierType;
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
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClassifierServiceImplTest {

  private static final long REQUEST_ID = 1L;

  @Mock
  private Orchestrator orchestrator;
  @Mock
  private Engine engine;
  @Mock
  private ClassifierType classifierType;

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
    when(classifierType.getStartAction()).thenReturn("startAction");

    service.start(request, classifierType);

    InOrder inOrder = buildInOrder();
    inOrder.verify(classifierType).getStartAction();
    inOrder.verify(orchestrator).bookEngine(REQUEST_ID, "startAction");
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("startAction"), any());
    inOrder.verify(engine).start(buildClassifierStartRequest(), classifierType);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_result_on_start() {
    ClassifierStartRequest request = buildClassifierStartRequest();
    onBookEngineReturn(orchestrator, engine, REQUEST_ID);
    onRunOnEngineCallMethod(orchestrator, engine, REQUEST_ID);
    when(classifierType.getStartAction()).thenReturn("startAction");

    ClassifierStartResponse actualResponse = service.start(request, classifierType);

    assertEquals(buildExpectedClassifierStartResponse(), actualResponse);
  }

  @Test
  public void should_release_and_cancel_request_on_start_failure() {
    ClassifierStartRequest request = buildClassifierStartRequest();
    onBookEngineReturn(orchestrator, engine, REQUEST_ID);
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "startAction", "start exception message");
    when(classifierType.getStartAction()).thenReturn("startAction");
    when(classifierType.getCancelAction()).thenReturn("cancelAction");

    try {
      service.start(request, classifierType);
      fail("should throw exception");

    } catch (Exception ignored) {
    }
    InOrder inOrder = buildInOrder();
    inOrder.verify(classifierType).getStartAction();
    inOrder.verify(orchestrator).bookEngine(REQUEST_ID, "startAction");
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("startAction"), any());
    inOrder.verify(engine).start(buildClassifierStartRequest(), classifierType);
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("cancelAction"), any());
    inOrder.verify(engine).cancel(classifierType);
    inOrder.verify(orchestrator).completeRequest(REQUEST_ID, "startAction");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_classifier_service_exception_on_start_failure() {
    ClassifierStartRequest request = buildClassifierStartRequest();
    onBookEngineReturn(orchestrator, engine, REQUEST_ID);
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "startAction", "start exception message");
    when(classifierType.getStartAction()).thenReturn("startAction");
    when(classifierType.getCancelAction()).thenReturn("cancelAction");

    try {
      service.start(request, classifierType);
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
    doThrowExceptionOnReleaseEngine(orchestrator, REQUEST_ID, "startAction", new NoBlockedEngineException("ignored exception"));
    when(classifierType.getStartAction()).thenReturn("startAction");
    when(classifierType.getCancelAction()).thenReturn("cancelAction");

    try {
      service.start(request, classifierType);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "start exception message");
    }
  }

  @Test
  public void should_delegate_call_to_orchestrator_and_engine_on_data() {
    ClassifierDataRequest request = buildClassifierDataRequest();
    onRunOnEngineCallMethod(orchestrator, engine, REQUEST_ID);
    when(classifierType.getDataAction()).thenReturn("dataAction");

    service.data(request, classifierType);

    InOrder inOrder = buildInOrder();
    inOrder.verify(classifierType).getDataAction();
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("dataAction"), any());
    inOrder.verify(engine).data(buildClassifierDataRequest(), classifierType);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_release_and_cancel_request_on_data_failure() {
    ClassifierDataRequest request = buildClassifierDataRequest();
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "dataAction", "data exception message");
    when(classifierType.getDataAction()).thenReturn("dataAction");
    when(classifierType.getCancelAction()).thenReturn("cancelAction");

    try {
      service.data(request, classifierType);
      fail("should throw exception");

    } catch (Exception ignored) {
    }
    InOrder inOrder = buildInOrder();
    inOrder.verify(classifierType).getDataAction();
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("dataAction"), any());
    inOrder.verify(engine).data(buildClassifierDataRequest(), classifierType);
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("cancelAction"), any());
    inOrder.verify(engine).cancel(classifierType);
    inOrder.verify(orchestrator).completeRequest(REQUEST_ID, "dataAction");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_classifier_service_exception_on_data_failure() {
    ClassifierDataRequest request = buildClassifierDataRequest();
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "dataAction", "data exception message");
    when(classifierType.getDataAction()).thenReturn("dataAction");
    when(classifierType.getCancelAction()).thenReturn("cancelAction");

    try {
      service.data(request, classifierType);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "data exception message");
    }
  }

  @Test
  public void should_ignore_if_release_throws_no_booked_engine_exception_on_data_failure() {
    ClassifierDataRequest request = buildClassifierDataRequest();
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "dataAction", "data exception message");
    doThrowExceptionOnReleaseEngine(orchestrator, REQUEST_ID, "dataAction", new NoBlockedEngineException("ignored exception"));
    when(classifierType.getDataAction()).thenReturn("dataAction");
    when(classifierType.getCancelAction()).thenReturn("cancelAction");

    try {
      service.data(request, classifierType);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "data exception message");
    }
  }

  @Test
  public void should_delegate_call_to_orchestrator_and_engine_on_predict() {
    ClassifierRequest request = buildClassifierRequest();
    onRunOnEngineCallMethod(orchestrator, engine, REQUEST_ID);
    when(classifierType.getPredictAction()).thenReturn("predictAction");

    service.predict(request, classifierType);

    InOrder inOrder = buildInOrder();
    inOrder.verify(classifierType).getPredictAction();
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("predictAction"), any());
    inOrder.verify(engine).predict(classifierType);
    inOrder.verify(orchestrator).completeRequest(REQUEST_ID, "predictAction");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_result_on_predict() {
    ClassifierRequest request = buildClassifierRequest();
    onRunOnEngineCallMethod(orchestrator, engine, REQUEST_ID);
    onEnginePredictReturn(buildClassifierResponse());
    when(classifierType.getPredictAction()).thenReturn("predictAction");

    ClassifierResponse actualResponse = service.predict(request, classifierType);

    assertEquals(buildClassifierResponse(), actualResponse);
  }

  @Test
  public void should_release_and_cancel_request_on_predict_failure() {
    ClassifierRequest request = buildClassifierRequest();
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "predictAction", "predict exception message");
    when(classifierType.getPredictAction()).thenReturn("predictAction");
    when(classifierType.getCancelAction()).thenReturn("cancelAction");

    try {
      service.predict(request, classifierType);
      fail("should throw exception");

    } catch (Exception ignored) {
    }
    InOrder inOrder = buildInOrder();
    inOrder.verify(classifierType).getPredictAction();
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("predictAction"), any());
    inOrder.verify(engine).predict(classifierType);
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("cancelAction"), any());
    inOrder.verify(engine).cancel(classifierType);
    inOrder.verify(orchestrator).completeRequest(REQUEST_ID, "predictAction");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_classifier_service_exception_on_predict_failure() {
    ClassifierRequest request = buildClassifierRequest();
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "predictAction", "predict exception message");
    when(classifierType.getPredictAction()).thenReturn("predictAction");
    when(classifierType.getCancelAction()).thenReturn("cancelAction");

    try {
      service.predict(request, classifierType);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "predict exception message");
    }
  }

  @Test
  public void should_ignore_if_release_throws_no_booked_engine_exception_on_predict_failure() {
    ClassifierRequest request = buildClassifierRequest();
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "predictAction", "predict exception message");
    doThrowExceptionOnReleaseEngine(orchestrator, REQUEST_ID, "predictAction", new NoBlockedEngineException("ignored exception"));
    when(classifierType.getPredictAction()).thenReturn("predictAction");
    when(classifierType.getCancelAction()).thenReturn("cancelAction");

    try {
      service.predict(request, classifierType);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "predict exception message");
    }
  }

  @Test
  public void should_delegate_call_to_orchestrator_and_engine_on_predict_accuracy() {
    ClassifierRequest request = buildClassifierRequest();
    onRunOnEngineCallMethod(orchestrator, engine, REQUEST_ID);
    when(classifierType.getPredictAccuracyAction()).thenReturn("predictAccuracyAction");

    service.computePredictAccuracy(request, classifierType);

    InOrder inOrder = buildInOrder();
    inOrder.verify(classifierType).getPredictAccuracyAction();
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("predictAccuracyAction"), any());
    inOrder.verify(engine).computePredictAccuracy(classifierType);
    inOrder.verify(orchestrator).completeRequest(REQUEST_ID, "predictAccuracyAction");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_result_on_predict_accuracy() {
    ClassifierRequest request = buildClassifierRequest();
    onRunOnEngineCallMethod(orchestrator, engine, REQUEST_ID);
    onEngineComputePredictAccuracyReturn(45.1);
    when(classifierType.getPredictAccuracyAction()).thenReturn("predictAccuracyAction");

    Double actualAccuracy = service.computePredictAccuracy(request, classifierType);

    assertEquals(45.1, actualAccuracy);
  }

  @Test
  public void should_release_and_cancel_request_on_predict_accuracy_failure() {
    ClassifierRequest request = buildClassifierRequest();
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "predictAccuracyAction", "predict accuracy exception message");
    when(classifierType.getPredictAccuracyAction()).thenReturn("predictAccuracyAction");
    when(classifierType.getCancelAction()).thenReturn("cancelAction");

    try {
      service.computePredictAccuracy(request, classifierType);
      fail("should throw exception");

    } catch (Exception ignored) {
    }
    InOrder inOrder = buildInOrder();
    inOrder.verify(classifierType).getPredictAccuracyAction();
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("predictAccuracyAction"), any());
    inOrder.verify(engine).computePredictAccuracy(classifierType);
    inOrder.verify(orchestrator).runOnEngine(eq(REQUEST_ID), eq("cancelAction"), any());
    inOrder.verify(engine).cancel(classifierType);
    inOrder.verify(orchestrator).completeRequest(REQUEST_ID, "predictAccuracyAction");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_classifier_service_exception_on_predict_accuracy_failure() {
    ClassifierRequest request = buildClassifierRequest();
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "predictAccuracyAction", "predict accuracy exception message");
    when(classifierType.getPredictAccuracyAction()).thenReturn("predictAccuracyAction");
    when(classifierType.getCancelAction()).thenReturn("cancelAction");

    try {
      service.computePredictAccuracy(request, classifierType);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "predict accuracy exception message");
    }
  }

  @Test
  public void should_ignore_if_release_throws_no_booked_engine_exception_on_predict_accuracy_failure() {
    ClassifierRequest request = buildClassifierRequest();
    doThrowExceptionOnRunOnEngine(orchestrator, engine, REQUEST_ID, "predictAccuracyAction", "predict accuracy exception message");
    doThrowExceptionOnReleaseEngine(orchestrator, REQUEST_ID, "predictAccuracyAction", new NoBlockedEngineException("ignored exception"));
    when(classifierType.getPredictAccuracyAction()).thenReturn("predictAccuracyAction");
    when(classifierType.getCancelAction()).thenReturn("cancelAction");

    try {
      service.computePredictAccuracy(request, classifierType);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "predict accuracy exception message");
    }
  }

  private InOrder buildInOrder() {
    return inOrder(orchestrator, engine, classifierType);
  }

  private void onEnginePredictReturn(ClassifierResponse classifierResponse) {
    when(engine.predict(classifierType)).thenReturn(classifierResponse);
  }

  private void onEngineComputePredictAccuracyReturn(Double accuracy) {
    when(engine.computePredictAccuracy(classifierType)).thenReturn(accuracy);
  }

  private static ClassifierStartRequest buildClassifierStartRequest() {
    return new ClassifierStartRequest(REQUEST_ID, "predictionColumnName", newArrayList("col1"), 3);
  }

  private static ClassifierStartResponse buildExpectedClassifierStartResponse() {
    return new ClassifierStartResponse(REQUEST_ID);
  }

  private static ClassifierDataRequest buildClassifierDataRequest() {
    return new ClassifierDataRequest(REQUEST_ID, "columnName", newArrayList(0, 1));
  }

  private static ClassifierRequest buildClassifierRequest() {
    return new ClassifierRequest(REQUEST_ID);
  }

  private static ClassifierResponse buildClassifierResponse() {
    return new ClassifierResponse(REQUEST_ID, "columnName", newArrayList(0, 1));
  }

  private static void assertOnClassifierServiceException(Exception exception, String exceptionMessage) {
    assertInstanceOf(ClassifierServiceException.class, exception);
    assertEquals(exceptionMessage, exception.getMessage());
  }
}