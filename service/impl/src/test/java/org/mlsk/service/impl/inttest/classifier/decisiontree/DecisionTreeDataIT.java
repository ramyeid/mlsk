package org.mlsk.service.impl.inttest.classifier.decisiontree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.classifier.model.ClassifierDataRequestModel;
import org.mlsk.api.classifier.model.ClassifierStartRequestModel;
import org.mlsk.api.classifier.model.ClassifierStartResponseModel;
import org.mlsk.api.decisiontree.api.DecisionTreeApi;
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
import static java.lang.String.valueOf;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mlsk.service.classifier.resource.decisiontree.DecisionTreeConstants.*;
import static org.mlsk.service.impl.inttest.MockEngine.MockedRequest.buildFailingMockRequest;
import static org.mlsk.service.impl.inttest.MockEngine.MockedRequest.buildMockRequest;
import static org.mlsk.service.impl.inttest.classifier.decisiontree.helper.DecisionTreeHelper.*;
import static org.mlsk.service.impl.testhelper.ResponseEntityHelper.assertOnResponseEntity;
import static org.mlsk.service.model.engine.EngineState.BOOKED;
import static org.mlsk.service.model.engine.EngineState.WAITING;

@ExtendWith(MockitoExtension.class)
public class DecisionTreeDataIT extends AbstractIT {

  private DecisionTreeApi decisionTreeApi;

  @BeforeEach
  public void setUp() throws Exception {
    super.setup(newArrayList(SERVICE_INFO1, SERVICE_INFO2));
    ClassifierService service = new ClassifierServiceImpl(orchestrator);
    decisionTreeApi = new DecisionTreeApiImpl(service);
  }

  // --------------------------------------------
  //                  DATA
  //---------------------------------------------

  @Test
  public void should_delegate_data_call_to_engine_with_request_id() throws Exception {
    String requestId1 = valueOf(SERVICE_INFO1.hashCode());
    String requestId2 = valueOf(SERVICE_INFO2.hashCode());
    ClassifierStartRequestModel startRequestModel = buildClassifierStartRequestModel();
    MockEngine.MockedRequest startRequest1 = buildMockRequest(SERVICE_INFO1, START_URL, buildClassifierStartRequest(), buildDefaultResponse());
    MockEngine.MockedRequest startRequest2 = buildMockRequest(SERVICE_INFO2, START_URL, buildClassifierStartRequest(), buildDefaultResponse());
    MockEngine.MockedRequest dataRequest1 = buildMockRequest(SERVICE_INFO1, DATA_URL, buildClassifierData1Request(requestId1), buildDefaultResponse());
    MockEngine.MockedRequest dataRequest2 = buildMockRequest(SERVICE_INFO2, DATA_URL, buildClassifierData2Request(requestId2), buildDefaultResponse());
    mockEngine.registerRequests(startRequest1, startRequest2, dataRequest1, dataRequest2);

    decisionTreeApi.start(startRequestModel);
    decisionTreeApi.start(startRequestModel);
    decisionTreeApi.data(buildClassifierData2RequestModel(requestId2));
    decisionTreeApi.data(buildClassifierData1RequestModel(requestId1));

    InOrder inOrder = buildInOrder();
    verifyServiceSetup(newArrayList(SERVICE_INFO1, SERVICE_INFO2), inOrder);
    verifyRestTemplateCalledOn(SERVICE_INFO1.getUrl() + START_URL, inOrder);
    verifyRestTemplateCalledOn(SERVICE_INFO2.getUrl() + START_URL, inOrder);
    verifyRestTemplateCalledOn(SERVICE_INFO2.getUrl() + DATA_URL, inOrder);
    verifyRestTemplateCalledOn(SERVICE_INFO1.getUrl() + DATA_URL, inOrder);
    inOrder.verifyNoMoreInteractions();
    assertOnEngineState(BOOKED, BOOKED);
  }

  @Test
  public void should_throw_exception_if_request_id_not_available_on_data() {
    String unavailableRequestId = "unavailableRequestId";
    ClassifierStartRequestModel startRequestModel = buildClassifierStartRequestModel();
    MockEngine.MockedRequest startRequest1 = buildMockRequest(SERVICE_INFO1, START_URL, buildClassifierStartRequest(), buildDefaultResponse());
    MockEngine.MockedRequest startRequest2 = buildMockRequest(SERVICE_INFO2, START_URL, buildClassifierStartRequest(), buildDefaultResponse());
    mockEngine.registerRequests(startRequest1, startRequest2);

    try {
      decisionTreeApi.start(startRequestModel);
      decisionTreeApi.start(startRequestModel);
      decisionTreeApi.data(buildClassifierData2RequestModel(unavailableRequestId));
      fail("should fail since requestId is not available");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "No available engine with unavailableRequestId to run decision-tree-data");
      assertOnEngineState(BOOKED, BOOKED);
    }
  }

  @Test
  public void should_throw_exception_if_engine_not_booked_on_data() {
    String requestId2 = valueOf(SERVICE_INFO2.hashCode());
    ClassifierStartRequestModel startRequestModel = buildClassifierStartRequestModel();
    MockEngine.MockedRequest startRequest1 = buildMockRequest(SERVICE_INFO1, START_URL, buildClassifierStartRequest(), buildDefaultResponse());
    mockEngine.registerRequests(startRequest1);

    try {
      decisionTreeApi.start(startRequestModel);
      decisionTreeApi.data(buildClassifierData1RequestModel(requestId2));
      fail("should fail since engine2 is not booked");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "No available engine with 1311893757 to run decision-tree-data");
      assertOnEngineState(BOOKED, WAITING);
    }
  }

  @Test
  public void should_throw_exception_if_engine_returns_an_exception_on_data() {
    String requestId = valueOf(SERVICE_INFO1.hashCode());
    ClassifierStartRequestModel startRequestModel = buildClassifierStartRequestModel();
    ClassifierDataRequestModel data1RequestModel = buildClassifierData1RequestModel(requestId);
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.BAD_REQUEST, "Exception NPE raised while pushing data: NullPointer");
    MockEngine.MockedRequest startRequest = buildMockRequest(SERVICE_INFO1, START_URL, buildClassifierStartRequest(), buildDefaultResponse());
    MockEngine.MockedRequest dataRequest = buildFailingMockRequest(SERVICE_INFO1, DATA_URL, buildClassifierData1Request(requestId), exceptionToThrow);
    MockEngine.MockedRequest cancelRequest = buildMockRequest(SERVICE_INFO1, CANCEL_URL, null, buildDefaultResponse());
    mockEngine.registerRequests(startRequest, dataRequest, cancelRequest);

    try {
      decisionTreeApi.start(startRequestModel);
      decisionTreeApi.data(data1RequestModel);
      fail("should fail since engine threw exception on data");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "Failed on post data to engine: Exception NPE raised while pushing data: NullPointer");
      assertOnEngineState(WAITING, WAITING);
    }
  }

  @Test
  public void should_release_engine_on_exception_on_data() {
    String requestId1 = valueOf(SERVICE_INFO1.hashCode());
    String requestId2 = valueOf(SERVICE_INFO2.hashCode());
    ClassifierStartRequestModel startRequestModel = buildClassifierStartRequestModel();
    ClassifierDataRequestModel data1RequestModel = buildClassifierData1RequestModel(requestId1);
    ClassifierDataRequestModel data2RequestModel = buildClassifierData2RequestModel(requestId2);
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.BAD_REQUEST, "Exception NPE raised while pushing data: NullPointer");
    MockEngine.MockedRequest startRequest1 = buildMockRequest(SERVICE_INFO1, START_URL, buildClassifierStartRequest(), buildDefaultResponse());
    MockEngine.MockedRequest startRequest2 = buildMockRequest(SERVICE_INFO2, START_URL, buildClassifierStartRequest(), buildDefaultResponse());
    MockEngine.MockedRequest failingDataRequest = buildFailingMockRequest(SERVICE_INFO1, DATA_URL, buildClassifierData1Request(requestId1), exceptionToThrow);
    MockEngine.MockedRequest dataRequest1 = buildMockRequest(SERVICE_INFO1, DATA_URL, buildClassifierData1Request(requestId1), buildDefaultResponse());
    MockEngine.MockedRequest dataRequest2 = buildMockRequest(SERVICE_INFO2, DATA_URL, buildClassifierData2Request(requestId2), buildDefaultResponse());
    MockEngine.MockedRequest cancelRequest = buildMockRequest(SERVICE_INFO1, CANCEL_URL, null, buildDefaultResponse());
    mockEngine.registerRequests(startRequest1, failingDataRequest, cancelRequest);

    decisionTreeApi.start(startRequestModel);
    ignoreException(() -> decisionTreeApi.data(data1RequestModel));
    mockEngine.overrideRequests(startRequest1, startRequest2, dataRequest1, dataRequest2);
    ResponseEntity<ClassifierStartResponseModel> actualStartResponse1 = decisionTreeApi.start(startRequestModel);
    ResponseEntity<ClassifierStartResponseModel> actualStartResponse2 = decisionTreeApi.start(startRequestModel);
    decisionTreeApi.data(data1RequestModel);
    decisionTreeApi.data(data2RequestModel);

    assertOnResponseEntity(buildClassifierStartResponseModel(requestId1), actualStartResponse1);
    assertOnResponseEntity(buildClassifierStartResponseModel(requestId2), actualStartResponse2);
    assertOnEngineState(BOOKED, BOOKED);
  }

  @Test
  public void should_call_cancel_on_exception_on_data() throws Exception {
    String requestId = valueOf(SERVICE_INFO1.hashCode());
    ClassifierStartRequestModel startRequestModel = buildClassifierStartRequestModel();
    ClassifierDataRequestModel dataRequestModel = buildClassifierData1RequestModel(requestId);
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.BAD_REQUEST, "Exception NPE raised while pushing data: NullPointer");
    MockEngine.MockedRequest startRequest = buildMockRequest(SERVICE_INFO1, START_URL, buildClassifierStartRequest(), buildDefaultResponse());
    MockEngine.MockedRequest failingDataRequest = buildFailingMockRequest(SERVICE_INFO1, DATA_URL, buildClassifierData1Request(requestId), exceptionToThrow);
    MockEngine.MockedRequest cancelRequest = buildMockRequest(SERVICE_INFO1, CANCEL_URL, null, buildDefaultResponse());
    mockEngine.registerRequests(startRequest, failingDataRequest, cancelRequest);

    decisionTreeApi.start(startRequestModel);
    ignoreException(() -> decisionTreeApi.data(dataRequestModel));

    InOrder inOrder = buildInOrder();
    verifyServiceSetup(newArrayList(SERVICE_INFO1, SERVICE_INFO2), inOrder);
    verifyRestTemplateCalledOn(SERVICE_INFO1.getUrl() + START_URL, inOrder);
    verifyRestTemplateCalledOn(SERVICE_INFO1.getUrl() + DATA_URL, inOrder);
    verifyRestTemplateCalledOn(SERVICE_INFO1.getUrl() + CANCEL_URL, inOrder);
    inOrder.verifyNoMoreInteractions();
    assertOnEngineState(WAITING, WAITING);
  }
}