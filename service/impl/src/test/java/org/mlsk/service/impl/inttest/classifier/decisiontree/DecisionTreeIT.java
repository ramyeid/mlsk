package org.mlsk.service.impl.inttest.classifier.decisiontree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.service.classifier.model.ClassifierDataRequestModel;
import org.mlsk.api.service.classifier.model.ClassifierResponseModel;
import org.mlsk.api.service.classifier.model.ClassifierRequestModel;
import org.mlsk.api.service.classifier.model.ClassifierStartRequestModel;
import org.mlsk.api.service.classifier.decisiontree.api.DecisionTreeApi;
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
import static java.math.BigDecimal.valueOf;
import static org.mlsk.service.model.classifier.utils.ClassifierConstants.*;
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
    ClassifierStartRequestModel startRequestModel = buildServiceClassifierStartRequestModel();
    ClassifierDataRequestModel data1RequestModel = buildServiceClassifierData1RequestModel(requestId1);
    ClassifierDataRequestModel data2RequestModel = buildServiceClassifierData2RequestModel(requestId2);
    ClassifierDataRequestModel data3RequestModel = buildServiceClassifierData1RequestModel(requestId3);
    ClassifierRequestModel request1Model = buildServiceClassifierRequestModel(requestId1);
    ClassifierRequestModel request2Model = buildServiceClassifierRequestModel(requestId2);
    ClassifierRequestModel request3Model = buildServiceClassifierRequestModel(requestId3);
    MockEngine.MockedRequest startRequest1 = buildMockRequest(ENDPOINT1, START_URL, buildEngineClassifierStartRequestModel(requestId1), buildDefaultResponse());
    MockEngine.MockedRequest startRequest2 = buildMockRequest(ENDPOINT2, START_URL, buildEngineClassifierStartRequestModel(requestId2), buildDefaultResponse());
    MockEngine.MockedRequest startRequest3 = buildMockRequest(ENDPOINT1, START_URL, buildEngineClassifierStartRequestModel(requestId3), buildDefaultResponse());
    MockEngine.MockedRequest dataRequest1 = buildMockRequest(ENDPOINT1, DATA_URL, buildEngineClassifierData1RequestModel(requestId1), buildDefaultResponse());
    MockEngine.MockedRequest dataRequest2 = buildMockRequest(ENDPOINT2, DATA_URL, buildEngineClassifierData2RequestModel(requestId2), buildDefaultResponse());
    MockEngine.MockedRequest dataRequest3 = buildMockRequest(ENDPOINT1, DATA_URL, buildEngineClassifierData1RequestModel(requestId3), buildDefaultResponse());
    MockEngine.MockedRequest predictRequest1 = buildMockRequest(ENDPOINT1, PREDICT_URL, buildEngineClassifierRequestModel(requestId1), buildEngineClassifierResponseModel(requestId1));
    MockEngine.MockedRequest predictAccuracyRequest1 = buildMockRequest(ENDPOINT1, PREDICT_ACCURACY_URL, buildEngineClassifierRequestModel(requestId3), valueOf(94.123));
    MockEngine.MockedRequest predictAccuracyRequest2 = buildMockRequest(ENDPOINT2, PREDICT_ACCURACY_URL, buildEngineClassifierRequestModel(requestId2), valueOf(123.1));
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

    assertOnResponseEntity(buildServiceClassifierResponseModel(requestId1), actualPredict1Future.get());
    assertOnResponseEntity(valueOf(123.1), actualPredictAccuracy2Future.get());
    assertOnResponseEntity(valueOf(94.123), actualPredictAccuracy1Future.get());
  }
}
