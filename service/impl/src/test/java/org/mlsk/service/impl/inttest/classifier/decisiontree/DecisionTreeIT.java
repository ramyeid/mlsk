package org.mlsk.service.impl.inttest.classifier.decisiontree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.classifier.model.ClassifierDataRequestModel;
import org.mlsk.api.classifier.model.ClassifierResponseModel;
import org.mlsk.api.classifier.model.ClassifierRequestModel;
import org.mlsk.api.classifier.model.ClassifierStartRequestModel;
import org.mlsk.api.decisiontree.api.DecisionTreeApi;
import org.mlsk.service.classifier.ClassifierService;
import org.mlsk.service.impl.classifier.api.decisiontree.DecisionTreeApiImpl;
import org.mlsk.service.impl.classifier.service.ClassifierServiceImpl;
import org.mlsk.service.impl.inttest.AbstractIT;
import org.mlsk.service.impl.inttest.MockEngine;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static com.google.common.collect.Lists.newArrayList;
import static org.mlsk.service.classifier.resource.decisiontree.DecisionTreeConstants.*;
import static org.mlsk.service.impl.inttest.MockEngine.MockedRequest.buildMockRequest;
import static org.mlsk.service.impl.inttest.classifier.decisiontree.helper.DecisionTreeHelper.*;
import static org.mlsk.service.impl.testhelper.ResponseEntityHelper.assertOnResponseEntity;

@ExtendWith(MockitoExtension.class)
public class DecisionTreeIT extends AbstractIT {

  private DecisionTreeApi decisionTreeApi;

  @BeforeEach
  public void setUp() throws Exception {
    super.setup(newArrayList(ENDPOINT1, ENDPOINT2));
    ClassifierService service = new ClassifierServiceImpl(orchestrator);
    decisionTreeApi = new DecisionTreeApiImpl(service);
  }

  // --------------------------------------------
  //                  MULTIPLE REQUESTS
  //---------------------------------------------

  @Test
  public void should_handle_multiple_requests() throws Exception {
    long requestId1 = 1L;
    long requestId2 = 2L;
    long requestId3 = 3L;
    ClassifierStartRequestModel startRequestModel = buildClassifierStartRequestModel();
    ClassifierDataRequestModel data1RequestModel = buildClassifierData1RequestModel(requestId1);
    ClassifierDataRequestModel data2RequestModel = buildClassifierData2RequestModel(requestId2);
    ClassifierDataRequestModel data3RequestModel = buildClassifierData1RequestModel(requestId3);
    ClassifierRequestModel request1Model = buildClassifierRequestModel(requestId1);
    ClassifierRequestModel request2Model = buildClassifierRequestModel(requestId2);
    ClassifierRequestModel request3Model = buildClassifierRequestModel(requestId3);
    MockEngine.MockedRequest startRequest1 = buildMockRequest(ENDPOINT1, START_URL, buildClassifierStartRequest(requestId1), buildDefaultResponse());
    MockEngine.MockedRequest startRequest2 = buildMockRequest(ENDPOINT2, START_URL, buildClassifierStartRequest(requestId2), buildDefaultResponse());
    MockEngine.MockedRequest startRequest3 = buildMockRequest(ENDPOINT1, START_URL, buildClassifierStartRequest(requestId3), buildDefaultResponse());
    MockEngine.MockedRequest dataRequest1 = buildMockRequest(ENDPOINT1, DATA_URL, buildClassifierData1Request(requestId1), buildDefaultResponse());
    MockEngine.MockedRequest dataRequest2 = buildMockRequest(ENDPOINT2, DATA_URL, buildClassifierData2Request(requestId2), buildDefaultResponse());
    MockEngine.MockedRequest dataRequest3 = buildMockRequest(ENDPOINT1, DATA_URL, buildClassifierData1Request(requestId3), buildDefaultResponse());
    MockEngine.MockedRequest predictRequest1 = buildMockRequest(ENDPOINT1, PREDICT_URL, null, buildClassifierResponse(requestId1));
    MockEngine.MockedRequest predictAccuracyRequest1 = buildMockRequest(ENDPOINT1, PREDICT_ACCURACY_URL, null, 94.123);
    MockEngine.MockedRequest predictAccuracyRequest2 = buildMockRequest(ENDPOINT2, PREDICT_ACCURACY_URL, null, 123.1);
    mockEngine.registerRequests(startRequest1, startRequest2, dataRequest1, predictRequest1, dataRequest2, predictAccuracyRequest1, startRequest3, dataRequest3, predictAccuracyRequest2);

    decisionTreeApi.start(startRequestModel);
    decisionTreeApi.start(startRequestModel);
    CompletableFuture<ResponseEntity<ClassifierResponseModel>> actualPredict1Future = async(() -> {
      decisionTreeApi.data(data1RequestModel);
      return decisionTreeApi.predict(request1Model);
    });
    CompletableFuture<ResponseEntity<BigDecimal>> actualPredictAccuracy2Future = async(() -> {
      decisionTreeApi.data(data2RequestModel);
      return decisionTreeApi.computePredictAccuracy(request2Model);
    });
    actualPredict1Future.join();
    actualPredictAccuracy2Future.join();
    CompletableFuture<ResponseEntity<BigDecimal>> actualPredictAccuracy1Future = async(() -> {
      decisionTreeApi.start(startRequestModel);
      decisionTreeApi.data(data3RequestModel);
      return decisionTreeApi.computePredictAccuracy(request3Model);
    });
    actualPredictAccuracy1Future.join();

    assertOnResponseEntity(buildClassifierResponseModel(requestId1), actualPredict1Future.get());
    assertOnResponseEntity(BigDecimal.valueOf(123.1), actualPredictAccuracy2Future.get());
    assertOnResponseEntity(BigDecimal.valueOf(94.123), actualPredictAccuracy1Future.get());
  }
}