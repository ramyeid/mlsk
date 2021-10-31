package org.mlsk.service.impl.classifier.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.service.classifier.ClassifierType;
import org.mlsk.service.engine.Engine;
import org.mlsk.service.impl.classifier.service.exception.ClassifierServiceException;
import org.mlsk.service.impl.orchestrator.Orchestrator;
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

  private static final String REQUEST_ID = "requestId";

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
    onRunOnEngineAndBlockCallMethod(orchestrator, engine, REQUEST_ID);
    when(classifierType.getStartAction()).thenReturn("startAction");

    service.start(request, classifierType);

    InOrder inOrder = buildInOrder();
    inOrder.verify(classifierType).getStartAction();
    inOrder.verify(orchestrator).runOnEngineAndBlock(any(), eq("startAction"));
    inOrder.verify(engine).start(buildClassifierStartRequest(), classifierType);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_result_on_start() {
    ClassifierStartRequest request = buildClassifierStartRequest();
    onRunOnEngineAndBlockCallMethod(orchestrator, engine, REQUEST_ID);

    ClassifierStartResponse actualResponse = service.start(request, classifierType);

    assertEquals(buildExpectedClassifierStartResponse(), actualResponse);
  }

  @Test
  public void should_throw_classifier_service_exception_on_start_failure() {
    ClassifierStartRequest request = buildClassifierStartRequest();
    doThrowExceptionOnRunOnEngineAndBlock(orchestrator, "start exception message");

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
    onRunOnEngineAndBlockWithIdCallMethod(orchestrator, engine, REQUEST_ID);
    when(classifierType.getDataAction()).thenReturn("dataAction");

    service.data(request, classifierType);

    InOrder inOrder = buildInOrder();
    inOrder.verify(classifierType).getDataAction();
    inOrder.verify(orchestrator).runOnEngineAndBlock(eq(REQUEST_ID), any(), eq("dataAction"));
    inOrder.verify(engine).data(buildClassifierDataRequest(), classifierType);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_classifier_service_exception_on_data_failure() {
    ClassifierDataRequest request = buildClassifierDataRequest();
    doThrowExceptionOnRunOnEngineAndBlockWithId(orchestrator, REQUEST_ID, "data exception message");

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
    onRunOnEngineAndUnblockCallMethod(orchestrator, engine, REQUEST_ID);
    when(classifierType.getPredictAction()).thenReturn("predictAction");

    service.predict(request, classifierType);

    InOrder inOrder = buildInOrder();
    inOrder.verify(classifierType).getPredictAction();
    inOrder.verify(orchestrator).runOnEngineAndUnblock(eq(REQUEST_ID), any(), eq("predictAction"));
    inOrder.verify(engine).predict(classifierType);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_result_on_predict() {
    ClassifierRequest request = buildClassifierRequest();
    onRunOnEngineAndUnblockCallMethod(orchestrator, engine, REQUEST_ID);
    onEnginePredictReturn(buildClassifierDataResponse());

    ClassifierDataResponse actualResponse = service.predict(request, classifierType);

    assertEquals(buildClassifierDataResponse(), actualResponse);
  }

  @Test
  public void should_throw_classifier_service_exception_on_predict_failure() {
    ClassifierRequest request = buildClassifierRequest();
    doThrowExceptionOnRunOnEngineAndUnblock(orchestrator, REQUEST_ID, "predict exception message");

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
    onRunOnEngineAndUnblockCallMethod(orchestrator, engine, REQUEST_ID);
    when(classifierType.getPredictAccuracyAction()).thenReturn("predictAccuracyAction");

    service.computePredictAccuracy(request, classifierType);

    InOrder inOrder = buildInOrder();
    inOrder.verify(classifierType).getPredictAccuracyAction();
    inOrder.verify(orchestrator).runOnEngineAndUnblock(eq(REQUEST_ID), any(), eq("predictAccuracyAction"));
    inOrder.verify(engine).computePredictAccuracy(classifierType);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_result_on_predict_accuracy() {
    ClassifierRequest request = buildClassifierRequest();
    onRunOnEngineAndUnblockCallMethod(orchestrator, engine, REQUEST_ID);
    onEngineComputePredictAccuracyReturn(123.1);

    Double actualResponse = service.computePredictAccuracy(request, classifierType);

    assertEquals(123.1, actualResponse);
  }

  @Test
  public void should_throw_classifier_service_exception_on_predict_accuracy_failure() {
    ClassifierRequest request = buildClassifierRequest();
    doThrowExceptionOnRunOnEngineAndUnblock(orchestrator, REQUEST_ID, "predict accuracy exception message");

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

  private void onEnginePredictReturn(ClassifierDataResponse classifierDataResponse) {
    when(engine.predict(classifierType)).thenReturn(classifierDataResponse);
  }

  private void onEngineComputePredictAccuracyReturn(Double accuracy) {
    when(engine.computePredictAccuracy(classifierType)).thenReturn(accuracy);
  }

  private static ClassifierStartRequest buildClassifierStartRequest() {
    return new ClassifierStartRequest("predictionColumnName", newArrayList("col1"), 3);
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

  private static ClassifierDataResponse buildClassifierDataResponse() {
    return new ClassifierDataResponse("columnName", newArrayList(0, 1));
  }

  private static void assertOnClassifierServiceException(Exception exception, String exceptionMessage) {
    assertInstanceOf(ClassifierServiceException.class, exception);
    assertEquals(exceptionMessage, exception.getMessage());
  }
}