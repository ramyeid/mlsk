package org.mlsk.service.impl.inttest.classifier.decisiontree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.service.classifier.model.*;
import org.mlsk.api.service.classifier.decisiontree.api.DecisionTreeApi;
import org.mlsk.service.classifier.ClassifierService;
import org.mlsk.service.impl.classifier.api.decisiontree.DecisionTreeApiImpl;
import org.mlsk.service.impl.classifier.service.ClassifierServiceImpl;
import org.mlsk.service.impl.inttest.AbstractIT;
import org.mlsk.service.impl.inttest.MockEngine;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mlsk.service.classifier.resource.decisiontree.DecisionTreeConstants.*;
import static org.mlsk.service.impl.inttest.MockEngine.MockedRequest.buildFailingMockRequest;
import static org.mlsk.service.impl.inttest.MockEngine.MockedRequest.buildMockRequest;
import static org.mlsk.service.impl.inttest.classifier.decisiontree.helper.DecisionTreeHelper.*;
import static org.mlsk.service.impl.testhelper.ResponseEntityHelper.assertOnResponseEntity;
import static org.mlsk.service.model.engine.EngineState.BOOKED;
import static org.mlsk.service.model.engine.EngineState.WAITING;

@ExtendWith(MockitoExtension.class)
public class DecisionTreePredictIT extends AbstractIT {

  private DecisionTreeApi decisionTreeApi;

  @BeforeEach
  public void setUp() throws Exception {
    super.setup(newArrayList(ENDPOINT1, ENDPOINT2));
    ClassifierService service = new ClassifierServiceImpl(orchestrator);
    decisionTreeApi = new DecisionTreeApiImpl(service);
  }

  // --------------------------------------------
  //                  PREDICT
  //---------------------------------------------

  @Test
  public void should_return_classifier_result_from_engine_on_predict() {
    long requestId = 1L;
    ClassifierStartRequestModel startRequestModel = buildClassifierStartRequestModel();
    ClassifierDataRequestModel data1RequestModel = buildClassifierData1RequestModel(requestId);
    ClassifierDataRequestModel data2RequestModel = buildClassifierData2RequestModel(requestId);
    ClassifierRequestModel requestModel = buildClassifierRequestModel(requestId);
    MockEngine.MockedRequest startRequest = buildMockRequest(ENDPOINT1, START_URL, buildClassifierStartRequest(requestId), buildDefaultResponse());
    MockEngine.MockedRequest data1Request = buildMockRequest(ENDPOINT1, DATA_URL, buildClassifierData1Request(requestId), buildDefaultResponse());
    MockEngine.MockedRequest data2Request = buildMockRequest(ENDPOINT1, DATA_URL, buildClassifierData2Request(requestId), buildDefaultResponse());
    MockEngine.MockedRequest predictRequest = buildMockRequest(ENDPOINT1, PREDICT_URL, buildClassifierRequest(requestId), buildClassifierResponse(requestId));
    mockEngine.registerRequests(startRequest, data1Request, data2Request, predictRequest);

    ResponseEntity<ClassifierStartResponseModel> actualStartResponse = decisionTreeApi.start(startRequestModel);
    decisionTreeApi.data(data1RequestModel);
    decisionTreeApi.data(data2RequestModel);
    ResponseEntity<ClassifierResponseModel> actualPredictResponse = decisionTreeApi.predict(requestModel);

    assertOnResponseEntity(buildClassifierStartResponseModel(requestId), actualStartResponse);
    assertOnResponseEntity(buildClassifierResponseModel(requestId), actualPredictResponse);
    assertOnEngineState(WAITING, WAITING);
  }

  @Test
  public void should_release_engine_on_predict() {
    long requestId1 = 1L;
    long requestId2 = 2L;
    long requestId3 = 3L;
    ClassifierStartRequestModel startRequestModel = buildClassifierStartRequestModel();
    ClassifierDataRequestModel data1RequestModel = buildClassifierData1RequestModel(requestId1);
    ClassifierDataRequestModel data2RequestModel = buildClassifierData2RequestModel(requestId1);
    ClassifierRequestModel requestModel = buildClassifierRequestModel(requestId1);
    MockEngine.MockedRequest startRequest1 = buildMockRequest(ENDPOINT1, START_URL, buildClassifierStartRequest(requestId1), buildDefaultResponse());
    MockEngine.MockedRequest startRequest2 = buildMockRequest(ENDPOINT1, START_URL, buildClassifierStartRequest(requestId2), buildDefaultResponse());
    MockEngine.MockedRequest startRequest3 = buildMockRequest(ENDPOINT2, START_URL, buildClassifierStartRequest(requestId3), buildDefaultResponse());
    MockEngine.MockedRequest data1Request = buildMockRequest(ENDPOINT1, DATA_URL, buildClassifierData1Request(requestId1), buildDefaultResponse());
    MockEngine.MockedRequest data2Request = buildMockRequest(ENDPOINT1, DATA_URL, buildClassifierData2Request(requestId1), buildDefaultResponse());
    MockEngine.MockedRequest predictRequest = buildMockRequest(ENDPOINT1, PREDICT_URL, buildClassifierRequest(requestId1), buildClassifierResponse(requestId1));
    mockEngine.registerRequests(startRequest1, data1Request, data2Request, predictRequest, startRequest2, startRequest3);

    ResponseEntity<ClassifierStartResponseModel> actualStartResponse1 = decisionTreeApi.start(startRequestModel);
    decisionTreeApi.data(data1RequestModel);
    decisionTreeApi.data(data2RequestModel);
    decisionTreeApi.predict(requestModel);
    ResponseEntity<ClassifierStartResponseModel> actualStartResponse1_ = decisionTreeApi.start(startRequestModel);
    ResponseEntity<ClassifierStartResponseModel> actualStartResponse2 = decisionTreeApi.start(startRequestModel);

    assertOnResponseEntity(buildClassifierStartResponseModel(requestId1), actualStartResponse1);
    assertOnResponseEntity(buildClassifierStartResponseModel(requestId2), actualStartResponse1_);
    assertOnResponseEntity(buildClassifierStartResponseModel(requestId3), actualStartResponse2);
    assertOnEngineState(BOOKED, BOOKED);
  }

  @Test
  public void should_throw_exception_if_request_id_not_available_on_predict() {
    long requestId1 = 1L;
    long requestId2 = 2L;
    long unavailableRequestId = -1L;
    ClassifierStartRequestModel startRequestModel = buildClassifierStartRequestModel();
    ClassifierRequestModel requestModel = buildClassifierRequestModel(unavailableRequestId);
    MockEngine.MockedRequest startRequest1 = buildMockRequest(ENDPOINT1, START_URL, buildClassifierStartRequest(requestId1), buildDefaultResponse());
    MockEngine.MockedRequest startRequest2 = buildMockRequest(ENDPOINT2, START_URL, buildClassifierStartRequest(requestId2), buildDefaultResponse());
    mockEngine.registerRequests(startRequest1, startRequest2);

    try {
      decisionTreeApi.start(startRequestModel);
      decisionTreeApi.start(startRequestModel);
      decisionTreeApi.predict(requestModel);
      fail("should fail since requestId is not available");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "No available engine with -1 to run decision-tree-predict");
      assertOnEngineState(BOOKED, BOOKED);
    }
  }

  @Test
  public void should_throw_exception_if_engine_not_booked_on_predict() {
    long requestId1 = 1L;
    long requestId2 = 2L;
    ClassifierStartRequestModel startRequestModel = buildClassifierStartRequestModel();
    ClassifierRequestModel requestModel = buildClassifierRequestModel(requestId2);
    MockEngine.MockedRequest startRequest = buildMockRequest(ENDPOINT1, START_URL, buildClassifierStartRequest(requestId1), buildDefaultResponse());
    mockEngine.registerRequests(startRequest);

    try {
      decisionTreeApi.start(startRequestModel);
      decisionTreeApi.predict(requestModel);
      fail("should fail since engine2 is not booked");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "No available engine with 2 to run decision-tree-predict");
      assertOnEngineState(BOOKED, WAITING);
    }
  }

  @Test
  public void should_throw_exception_if_engine_returns_an_exception_on_predict() {
    long requestId = 1L;
    ClassifierStartRequestModel startRequestModel = buildClassifierStartRequestModel();
    ClassifierDataRequestModel data1RequestModel = buildClassifierData1RequestModel(requestId);
    ClassifierRequestModel requestModel = buildClassifierRequestModel(requestId);
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.BAD_REQUEST, "Exception NPE raised while pushing predict: NullPointer");
    MockEngine.MockedRequest startRequest = buildMockRequest(ENDPOINT1, START_URL, buildClassifierStartRequest(requestId), buildDefaultResponse());
    MockEngine.MockedRequest dataRequest = buildMockRequest(ENDPOINT1, DATA_URL, buildClassifierData1Request(requestId), buildDefaultResponse());
    MockEngine.MockedRequest failingPredictRequest = buildFailingMockRequest(ENDPOINT1, PREDICT_URL, buildClassifierRequest(requestId), exceptionToThrow);
    MockEngine.MockedRequest cancelRequest = buildMockRequest(ENDPOINT1, CANCEL_URL, buildClassifierCancelRequest(requestId), buildDefaultResponse());
    mockEngine.registerRequests(startRequest, dataRequest, failingPredictRequest, cancelRequest);

    try {
      decisionTreeApi.start(startRequestModel);
      decisionTreeApi.data(data1RequestModel);
      decisionTreeApi.predict(requestModel);
      fail("should fail since engine threw exception on predict");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "Failed on post predict to engine: Exception NPE raised while pushing predict: NullPointer");
      assertOnEngineState(WAITING, WAITING);
    }
  }

  @Test
  public void should_release_engine_on_exception_on_predict() {
    long requestId1 = 1L;
    long requestId2 = 2L;
    long requestId3 = 3L;
    ClassifierStartRequestModel startRequestModel = buildClassifierStartRequestModel();
    ClassifierDataRequestModel data1RequestModel = buildClassifierData1RequestModel(requestId1);
    ClassifierRequestModel requestModel = buildClassifierRequestModel(requestId1);
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.BAD_REQUEST, "Exception NPE raised while pushing predict: NullPointer");
    MockEngine.MockedRequest startRequest1 = buildMockRequest(ENDPOINT1, START_URL, buildClassifierStartRequest(requestId1), buildDefaultResponse());
    MockEngine.MockedRequest startRequest2 = buildMockRequest(ENDPOINT1, START_URL, buildClassifierStartRequest(requestId2), buildDefaultResponse());
    MockEngine.MockedRequest startRequest3 = buildMockRequest(ENDPOINT2, START_URL, buildClassifierStartRequest(requestId3), buildDefaultResponse());
    MockEngine.MockedRequest dataRequest1 = buildMockRequest(ENDPOINT1, DATA_URL, buildClassifierData1Request(requestId1), buildDefaultResponse());
    MockEngine.MockedRequest failingPredictRequest = buildFailingMockRequest(ENDPOINT1, PREDICT_URL, null, exceptionToThrow);
    MockEngine.MockedRequest cancelRequest = buildMockRequest(ENDPOINT1, CANCEL_URL, null, buildDefaultResponse());
    mockEngine.registerRequests(startRequest1, dataRequest1, failingPredictRequest, cancelRequest);

    decisionTreeApi.start(startRequestModel);
    decisionTreeApi.data(data1RequestModel);
    ignoreException(() -> decisionTreeApi.predict(requestModel));
    mockEngine.overrideRequests(startRequest2, startRequest3);
    ResponseEntity<ClassifierStartResponseModel> actualStartResponse1 = decisionTreeApi.start(startRequestModel);
    ResponseEntity<ClassifierStartResponseModel> actualStartResponse2 = decisionTreeApi.start(startRequestModel);

    assertOnResponseEntity(buildClassifierStartResponseModel(requestId2), actualStartResponse1);
    assertOnResponseEntity(buildClassifierStartResponseModel(requestId3), actualStartResponse2);
    assertOnEngineState(BOOKED, BOOKED);
  }

  @Test
  public void should_call_cancel_on_exception_on_predict() throws Exception {
    long requestId = 1L;
    ClassifierStartRequestModel startRequestModel = buildClassifierStartRequestModel();
    ClassifierDataRequestModel dataRequestModel = buildClassifierData1RequestModel(requestId);
    ClassifierRequestModel requestModel = buildClassifierRequestModel(requestId);
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.BAD_REQUEST, "Exception NPE raised while pushing predict: NullPointer");
    MockEngine.MockedRequest startRequest1 = buildMockRequest(ENDPOINT1, START_URL, buildClassifierStartRequest(requestId), buildDefaultResponse());
    MockEngine.MockedRequest dataRequest1 = buildMockRequest(ENDPOINT1, DATA_URL, buildClassifierData1Request(requestId), buildDefaultResponse());
    MockEngine.MockedRequest failingPredictRequest = buildFailingMockRequest(ENDPOINT1, PREDICT_URL, buildClassifierRequest(requestId), exceptionToThrow);
    MockEngine.MockedRequest cancelRequest = buildMockRequest(ENDPOINT1, CANCEL_URL, buildClassifierCancelRequest(requestId), buildDefaultResponse());
    mockEngine.registerRequests(startRequest1, dataRequest1, failingPredictRequest, cancelRequest);

    decisionTreeApi.start(startRequestModel);
    decisionTreeApi.data(dataRequestModel);
    ignoreException(() -> decisionTreeApi.predict(requestModel));

    InOrder inOrder = buildInOrder();
    verifyServiceSetup(newArrayList(ENDPOINT1, ENDPOINT2), inOrder);
    verifyRestTemplateCalledOn(ENDPOINT1.getUrl() + START_URL, inOrder);
    verifyRestTemplateCalledOn(ENDPOINT1.getUrl() + DATA_URL, inOrder);
    verifyRestTemplateCalledOn(ENDPOINT1.getUrl() + CANCEL_URL, inOrder);
    inOrder.verifyNoMoreInteractions();
    assertOnEngineState(WAITING, WAITING);
  }
}