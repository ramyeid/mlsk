package org.mlsk.service.impl.inttest.classifier.decisiontree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.service.classifier.model.ClassifierDataRequestModel;
import org.mlsk.api.service.classifier.model.ClassifierStartRequestModel;
import org.mlsk.api.service.classifier.model.ClassifierStartResponseModel;
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
import static org.mlsk.service.model.classifier.utils.ClassifierConstants.*;
import static org.mlsk.service.impl.inttest.MockEngine.MockedRequest.buildFailingMockRequest;
import static org.mlsk.service.impl.inttest.MockEngine.MockedRequest.buildMockRequest;
import static org.mlsk.service.impl.inttest.classifier.decisiontree.helper.DecisionTreeHelper.*;
import static org.mlsk.service.impl.testhelper.ResponseEntityHelper.assertOnResponseEntity;
import static org.mlsk.service.model.engine.EngineState.BOOKED;
import static org.mlsk.service.model.engine.EngineState.IDLE;

@ExtendWith(MockitoExtension.class)
public class DecisionTreeDataIT extends AbstractIT {

  private DecisionTreeApi decisionTreeApi;

  @BeforeEach
  public void setUp() throws Exception {
    super.setup(newArrayList(ENDPOINT1, ENDPOINT2));
    ClassifierService service = new ClassifierServiceImpl(orchestrator);
    decisionTreeApi = new DecisionTreeApiImpl(service);
  }

  // --------------------------------------------
  //                  DATA
  //---------------------------------------------

  @Test
  public void should_delegate_data_call_to_engine_with_request_id() throws Exception {
    long requestId1 = 1L;
    long requestId2 = 2L;
    ClassifierStartRequestModel startRequestModel = buildServiceClassifierStartRequestModel();
    MockEngine.MockedRequest startRequest1 = buildMockRequest(ENDPOINT1, START_URL, buildEngineClassifierStartRequestModel(requestId1), buildDefaultResponse());
    MockEngine.MockedRequest startRequest2 = buildMockRequest(ENDPOINT2, START_URL, buildEngineClassifierStartRequestModel(requestId2), buildDefaultResponse());
    MockEngine.MockedRequest dataRequest1 = buildMockRequest(ENDPOINT1, DATA_URL, buildEngineClassifierData1RequestModel(requestId1), buildDefaultResponse());
    MockEngine.MockedRequest dataRequest2 = buildMockRequest(ENDPOINT2, DATA_URL, buildEngineClassifierData2RequestModel(requestId2), buildDefaultResponse());
    mockEngine.registerRequests(startRequest1, startRequest2, dataRequest1, dataRequest2);

    decisionTreeApi.start(startRequestModel);
    decisionTreeApi.start(startRequestModel);
    decisionTreeApi.data(buildServiceClassifierData2RequestModel(requestId2));
    decisionTreeApi.data(buildServiceClassifierData1RequestModel(requestId1));

    InOrder inOrder = buildInOrder();
    verifyServiceSetup(newArrayList(ENDPOINT1, ENDPOINT2), inOrder);
    verifyRestTemplateCalledOn(ENDPOINT1.getUrl() + START_URL, inOrder);
    verifyRestTemplateCalledOn(ENDPOINT2.getUrl() + START_URL, inOrder);
    verifyRestTemplateCalledOn(ENDPOINT2.getUrl() + DATA_URL, inOrder);
    verifyRestTemplateCalledOn(ENDPOINT1.getUrl() + DATA_URL, inOrder);
    inOrder.verifyNoMoreInteractions();
    assertOnEngineState(BOOKED, BOOKED);
  }

  @Test
  public void should_throw_exception_if_request_id_not_available_on_data() {
    long requestId1 = 1L;
    long requestId2 = 2L;
    long unavailableRequestId = -1L;
    ClassifierStartRequestModel startRequestModel = buildServiceClassifierStartRequestModel();
    MockEngine.MockedRequest startRequest1 = buildMockRequest(ENDPOINT1, START_URL, buildEngineClassifierStartRequestModel(requestId1), buildDefaultResponse());
    MockEngine.MockedRequest startRequest2 = buildMockRequest(ENDPOINT2, START_URL, buildEngineClassifierStartRequestModel(requestId2), buildDefaultResponse());
    mockEngine.registerRequests(startRequest1, startRequest2);

    try {
      decisionTreeApi.start(startRequestModel);
      decisionTreeApi.start(startRequestModel);
      decisionTreeApi.data(buildServiceClassifierData2RequestModel(unavailableRequestId));
      fail("should fail since requestId is not available");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "No available engine with -1 to run decision-tree-data");
      assertOnEngineState(BOOKED, BOOKED);
    }
  }

  @Test
  public void should_throw_exception_if_engine_not_booked_on_data() {
    long requestId1 = 1L;
    long requestId2 = 2L;
    ClassifierStartRequestModel startRequestModel = buildServiceClassifierStartRequestModel();
    MockEngine.MockedRequest startRequest1 = buildMockRequest(ENDPOINT1, START_URL, buildEngineClassifierStartRequestModel(requestId1), buildDefaultResponse());
    mockEngine.registerRequests(startRequest1);

    try {
      decisionTreeApi.start(startRequestModel);
      decisionTreeApi.data(buildServiceClassifierData1RequestModel(requestId2));
      fail("should fail since engine2 is not booked");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "No available engine with 2 to run decision-tree-data");
      assertOnEngineState(BOOKED, IDLE);
    }
  }

  @Test
  public void should_throw_exception_if_engine_returns_an_exception_on_data() {
    long requestId = 1L;
    ClassifierStartRequestModel startRequestModel = buildServiceClassifierStartRequestModel();
    ClassifierDataRequestModel data1RequestModel = buildServiceClassifierData1RequestModel(requestId);
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.BAD_REQUEST, "Exception NPE raised while pushing data: NullPointer");
    MockEngine.MockedRequest startRequest = buildMockRequest(ENDPOINT1, START_URL, buildEngineClassifierStartRequestModel(requestId), buildDefaultResponse());
    MockEngine.MockedRequest dataRequest = buildFailingMockRequest(ENDPOINT1, DATA_URL, buildEngineClassifierData1RequestModel(requestId), exceptionToThrow);
    MockEngine.MockedRequest cancelRequest = buildMockRequest(ENDPOINT1, CANCEL_URL, buildEngineClassifierCancelRequestModel(requestId), buildDefaultResponse());
    mockEngine.registerRequests(startRequest, dataRequest, cancelRequest);

    try {
      decisionTreeApi.start(startRequestModel);
      decisionTreeApi.data(data1RequestModel);
      fail("should fail since engine threw exception on data");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "Failed on post data to engine: Exception NPE raised while pushing data: NullPointer");
      assertOnEngineState(IDLE, IDLE);
    }
  }

  @Test
  public void should_release_engine_on_exception_on_data() {
    long requestId1 = 1L;
    long requestId2 = 2L;
    long requestId3 = 3L;
    ClassifierStartRequestModel startRequestModel = buildServiceClassifierStartRequestModel();
    ClassifierDataRequestModel data1RequestModel = buildServiceClassifierData1RequestModel(requestId1);
    ClassifierDataRequestModel data2RequestModel = buildServiceClassifierData1RequestModel(requestId2);
    ClassifierDataRequestModel data3RequestModel = buildServiceClassifierData2RequestModel(requestId3);
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.BAD_REQUEST, "Exception NPE raised while pushing data: NullPointer");
    MockEngine.MockedRequest startRequest1 = buildMockRequest(ENDPOINT1, START_URL, buildEngineClassifierStartRequestModel(requestId1), buildDefaultResponse());
    MockEngine.MockedRequest startRequest2 = buildMockRequest(ENDPOINT1, START_URL, buildEngineClassifierStartRequestModel(requestId2), buildDefaultResponse());
    MockEngine.MockedRequest startRequest3 = buildMockRequest(ENDPOINT2, START_URL, buildEngineClassifierStartRequestModel(requestId3), buildDefaultResponse());
    MockEngine.MockedRequest failingDataRequest = buildFailingMockRequest(ENDPOINT1, DATA_URL, buildEngineClassifierData1RequestModel(requestId1), exceptionToThrow);
    MockEngine.MockedRequest dataRequest1 = buildMockRequest(ENDPOINT1, DATA_URL, buildEngineClassifierData1RequestModel(requestId2), buildDefaultResponse());
    MockEngine.MockedRequest dataRequest2 = buildMockRequest(ENDPOINT2, DATA_URL, buildEngineClassifierData2RequestModel(requestId3), buildDefaultResponse());
    MockEngine.MockedRequest cancelRequest = buildMockRequest(ENDPOINT1, CANCEL_URL, buildEngineClassifierCancelRequestModel(requestId1), buildDefaultResponse());
    mockEngine.registerRequests(startRequest1, failingDataRequest, cancelRequest);

    decisionTreeApi.start(startRequestModel);
    ignoreException(() -> decisionTreeApi.data(data1RequestModel));
    mockEngine.overrideRequests(startRequest2, startRequest3, dataRequest1, dataRequest2);
    ResponseEntity<ClassifierStartResponseModel> actualStartResponse1 = decisionTreeApi.start(startRequestModel);
    ResponseEntity<ClassifierStartResponseModel> actualStartResponse2 = decisionTreeApi.start(startRequestModel);
    decisionTreeApi.data(data2RequestModel);
    decisionTreeApi.data(data3RequestModel);

    assertOnResponseEntity(buildServiceClassifierStartResponseModel(requestId2), actualStartResponse1);
    assertOnResponseEntity(buildServiceClassifierStartResponseModel(requestId3), actualStartResponse2);
    assertOnEngineState(BOOKED, BOOKED);
  }

  @Test
  public void should_call_cancel_on_exception_on_data() throws Exception {
    long requestId = 1L;
    ClassifierStartRequestModel startRequestModel = buildServiceClassifierStartRequestModel();
    ClassifierDataRequestModel dataRequestModel = buildServiceClassifierData1RequestModel(requestId);
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.BAD_REQUEST, "Exception NPE raised while pushing data: NullPointer");
    MockEngine.MockedRequest startRequest = buildMockRequest(ENDPOINT1, START_URL, buildEngineClassifierStartRequestModel(requestId), buildDefaultResponse());
    MockEngine.MockedRequest failingDataRequest = buildFailingMockRequest(ENDPOINT1, DATA_URL, buildEngineClassifierData1RequestModel(requestId), exceptionToThrow);
    MockEngine.MockedRequest cancelRequest = buildMockRequest(ENDPOINT1, CANCEL_URL, buildEngineClassifierCancelRequestModel(requestId), buildDefaultResponse());
    mockEngine.registerRequests(startRequest, failingDataRequest, cancelRequest);

    decisionTreeApi.start(startRequestModel);
    ignoreException(() -> decisionTreeApi.data(dataRequestModel));

    InOrder inOrder = buildInOrder();
    verifyServiceSetup(newArrayList(ENDPOINT1, ENDPOINT2), inOrder);
    verifyRestTemplateCalledOn(ENDPOINT1.getUrl() + START_URL, inOrder);
    verifyRestTemplateCalledOn(ENDPOINT1.getUrl() + DATA_URL, inOrder);
    verifyRestTemplateCalledOn(ENDPOINT1.getUrl() + CANCEL_URL, inOrder);
    inOrder.verifyNoMoreInteractions();
    assertOnEngineState(IDLE, IDLE);
  }
}
