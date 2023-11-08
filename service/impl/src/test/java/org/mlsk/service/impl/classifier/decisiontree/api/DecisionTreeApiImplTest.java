package org.mlsk.service.impl.classifier.decisiontree.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.service.classifier.model.*;
import org.mlsk.service.classifier.ClassifierService;
import org.mlsk.service.impl.classifier.api.decisiontree.DecisionTreeApiImpl;
import org.mlsk.service.impl.orchestrator.request.generator.RequestIdGenerator;
import org.mlsk.service.model.classifier.*;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static com.google.common.collect.Lists.newArrayList;
import static java.math.BigDecimal.valueOf;
import static org.mlsk.service.classifier.ClassifierType.DECISION_TREE;
import static org.mlsk.service.impl.testhelper.ResponseEntityHelper.assertOnResponseEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DecisionTreeApiImplTest {

  @Mock
  private ClassifierService service;

  private DecisionTreeApiImpl decisionTreeApi;

  @BeforeEach
  public void setUp() {
    this.decisionTreeApi = new DecisionTreeApiImpl(service);
    RequestIdGenerator.reset(1L);
  }

  @Test
  public void should_delegate_call_to_service_on_start() {
    long requestId = 1L;
    ClassifierStartRequestModel model = buildClassifierStartRequestModel();
    onServiceStartReturn(buildClassifierStartResponse(requestId));

    decisionTreeApi.start(model);

    InOrder inOrder = buildInOrder();
    inOrder.verify(service).start(buildClassifierStartRequest(requestId), DECISION_TREE);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_response_on_start() {
    long requestId = 1L;
    ClassifierStartRequestModel model = buildClassifierStartRequestModel();
    onServiceStartReturn(buildClassifierStartResponse(requestId));

    ResponseEntity<ClassifierStartResponseModel> actualResponse = decisionTreeApi.start(model);

    assertOnResponseEntity(buildClassifierStartResponseModel(requestId), actualResponse);
  }

  @Test
  public void should_delegate_call_to_service_on_data() {
    long requestId = 1L;
    ClassifierDataRequestModel model = buildClassifierDataRequestModel(requestId);

    decisionTreeApi.data(model);

    InOrder inOrder = buildInOrder();
    inOrder.verify(service).data(buildClassifierDataRequest(requestId), DECISION_TREE);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_response_on_data() {
    ClassifierDataRequestModel model = buildClassifierDataRequestModel(1L);

    ResponseEntity<Void> actualResponse = decisionTreeApi.data(model);

    assertOnResponseEntity(null, actualResponse);
  }

  @Test
  public void should_delegate_call_to_service_on_predict() {
    long requestId = 1L;
    ClassifierRequestModel model = buildClassifierRequestModel(requestId);
    onServicePredictReturn(buildClassifierResponse(requestId));

    decisionTreeApi.predict(model);

    InOrder inOrder = buildInOrder();
    inOrder.verify(service).predict(buildClassifierRequest(requestId), DECISION_TREE);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_response_on_predict() {
    long requestId = 1L;
    ClassifierRequestModel model = buildClassifierRequestModel(requestId);
    onServicePredictReturn(buildClassifierResponse(requestId));

    ResponseEntity<ClassifierResponseModel> actualResponse = decisionTreeApi.predict(model);

    assertOnResponseEntity(buildClassifierResponseModel(requestId), actualResponse);
  }

  @Test
  public void should_delegate_call_to_service_on_compute_predict_accuracy() {
    long requestId = 1L;
    ClassifierRequestModel model = buildClassifierRequestModel(requestId);

    decisionTreeApi.computePredictAccuracy(model);

    InOrder inOrder = buildInOrder();
    inOrder.verify(service).computePredictAccuracy(buildClassifierRequest(requestId), DECISION_TREE);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_response_on_compute_predict_accuracy() {
    long requestId = 1L;
    ClassifierRequestModel model = buildClassifierRequestModel(requestId);
    onServiceComputePredictAccuracyReturn(98.432);

    ResponseEntity<BigDecimal> actualResponse = decisionTreeApi.computePredictAccuracy(model);

    assertOnResponseEntity(valueOf(98.432), actualResponse);
  }

  private void onServiceStartReturn(ClassifierStartResponse startResponse) {
    when(service.start(any(), eq(DECISION_TREE))).thenReturn(startResponse);
  }

  private void onServicePredictReturn(ClassifierResponse classifierResponse) {
    when(service.predict(any(), eq(DECISION_TREE))).thenReturn(classifierResponse);
  }

  private void onServiceComputePredictAccuracyReturn(double accuracy) {
    when(service.computePredictAccuracy(any(), eq(DECISION_TREE))).thenReturn(accuracy);
  }

  private InOrder buildInOrder() {
    return inOrder(service);
  }

  private static ClassifierStartRequestModel buildClassifierStartRequestModel() {
    return new ClassifierStartRequestModel("predictionColumn", newArrayList("col1", "col2"), 12);
  }

  private static ClassifierStartRequest buildClassifierStartRequest(long requestId) {
    return new ClassifierStartRequest(requestId, "predictionColumn", newArrayList("col1", "col2"), 12);
  }

  private static ClassifierStartResponse buildClassifierStartResponse(long requestId) {
    return new ClassifierStartResponse(requestId);
  }

  private static ClassifierStartResponseModel buildClassifierStartResponseModel(long requestId) {
    return new ClassifierStartResponseModel(requestId);
  }

  private static ClassifierDataRequestModel buildClassifierDataRequestModel(long requestId) {
    return new ClassifierDataRequestModel(requestId, "columnName", newArrayList(0, 1, 2));
  }

  private static ClassifierDataRequest buildClassifierDataRequest(long requestId) {
    return new ClassifierDataRequest(requestId, "columnName", newArrayList(0, 1, 2));
  }

  private static ClassifierRequestModel buildClassifierRequestModel(long requestId) {
    return new ClassifierRequestModel(requestId);
  }

  private static ClassifierRequest buildClassifierRequest(long requestId) {
    return new ClassifierRequest(requestId);
  }

  private static ClassifierResponseModel buildClassifierResponseModel(long requestId) {
    return new ClassifierResponseModel(requestId, "columnName", newArrayList(0, 1));
  }

  private static ClassifierResponse buildClassifierResponse(long requestId) {
    return new ClassifierResponse(requestId, "columnName", newArrayList(0, 1));
  }

}