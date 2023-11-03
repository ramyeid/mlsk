package org.mlsk.service.impl.inttest.classifier.decisiontree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mlsk.service.classifier.resource.decisiontree.DecisionTreeConstants.CANCEL_URL;
import static org.mlsk.service.classifier.resource.decisiontree.DecisionTreeConstants.START_URL;
import static org.mlsk.service.impl.inttest.MockEngine.MockedRequest.buildFailingMockRequest;
import static org.mlsk.service.impl.inttest.MockEngine.MockedRequest.buildMockRequest;
import static org.mlsk.service.impl.inttest.classifier.decisiontree.helper.DecisionTreeHelper.*;
import static org.mlsk.service.impl.testhelper.ResponseEntityHelper.assertOnResponseEntity;
import static org.mlsk.service.model.engine.EngineState.BOOKED;
import static org.mlsk.service.model.engine.EngineState.WAITING;


@ExtendWith(MockitoExtension.class)
public class DecisionTreeStartIT extends AbstractIT {

  private DecisionTreeApi decisionTreeApi;

  @BeforeEach
  public void setUp() throws Exception {
    super.setup(newArrayList(ENDPOINT1, ENDPOINT2));
    ClassifierService service = new ClassifierServiceImpl(orchestrator);
    decisionTreeApi = new DecisionTreeApiImpl(service);
  }

  // --------------------------------------------
  //                  START
  //---------------------------------------------

  @Test
  public void should_return_request_id_on_start() {
    String requestId = valueOf(ENDPOINT1.hashCode());
    ClassifierStartRequestModel startRequestModel = buildClassifierStartRequestModel();
    MockEngine.MockedRequest startRequest = buildMockRequest(ENDPOINT1, START_URL, buildClassifierStartRequest(), buildDefaultResponse());
    mockEngine.registerRequests(startRequest);

    ResponseEntity<ClassifierStartResponseModel> actualStartResponse = decisionTreeApi.start(startRequestModel);

    assertOnResponseEntity(buildClassifierStartResponseModel(requestId), actualStartResponse);
    assertOnEngineState(BOOKED, WAITING);
  }

  @Test
  public void should_throw_exception_if_engine_returns_an_exception_on_start() {
    ClassifierStartRequestModel startRequestModel = buildClassifierStartRequestModel();
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.BAD_REQUEST, "Exception NPE raised while starting: NullPointer");
    MockEngine.MockedRequest startRequest = buildFailingMockRequest(ENDPOINT1, START_URL, buildClassifierStartRequest(), exceptionToThrow);
    MockEngine.MockedRequest cancelRequest = buildMockRequest(ENDPOINT1, CANCEL_URL, null, buildDefaultResponse());
    mockEngine.registerRequests(startRequest, cancelRequest);

    try {
      decisionTreeApi.start(startRequestModel);
      fail("should fail since engine threw exception on start");

    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "Failed on post start to engine: Exception NPE raised while starting: NullPointer");
      assertOnEngineState(WAITING, WAITING);
    }
  }

  @Test
  public void should_release_engine_on_exception_on_start() {
    String requestId1 = valueOf(ENDPOINT1.hashCode());
    String requestId2 = valueOf(ENDPOINT2.hashCode());
    ClassifierStartRequestModel startRequestModel = buildClassifierStartRequestModel();
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.BAD_REQUEST, "Exception NPE raised while starting: NullPointer");
    MockEngine.MockedRequest failingStartRequest = buildFailingMockRequest(ENDPOINT1, START_URL, buildClassifierStartRequest(), exceptionToThrow);
    MockEngine.MockedRequest startRequest1 = buildMockRequest(ENDPOINT1, START_URL, buildClassifierStartRequest(), buildDefaultResponse());
    MockEngine.MockedRequest startRequest2 = buildMockRequest(ENDPOINT2, START_URL, buildClassifierStartRequest(), buildDefaultResponse());
    MockEngine.MockedRequest cancelRequest = buildMockRequest(ENDPOINT1, CANCEL_URL, null, buildDefaultResponse());
    mockEngine.registerRequests(failingStartRequest, cancelRequest);

    ignoreException(() -> decisionTreeApi.start(startRequestModel));
    mockEngine.overrideRequests(startRequest1, startRequest2);
    ResponseEntity<ClassifierStartResponseModel> actualStartResponse1 = decisionTreeApi.start(startRequestModel);
    ResponseEntity<ClassifierStartResponseModel> actualStartResponse2 = decisionTreeApi.start(startRequestModel);

    assertOnResponseEntity(buildClassifierStartResponseModel(requestId1), actualStartResponse1);
    assertOnResponseEntity(buildClassifierStartResponseModel(requestId2), actualStartResponse2);
    assertOnEngineState(BOOKED, BOOKED);
  }

  @Test
  public void should_call_cancel_on_exception_on_start() throws Exception {
    ClassifierStartRequestModel startRequestModel = buildClassifierStartRequestModel();
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.BAD_REQUEST, "Exception NPE raised while starting: NullPointer");
    MockEngine.MockedRequest failingStartRequest = buildFailingMockRequest(ENDPOINT1, START_URL, buildClassifierStartRequest(), exceptionToThrow);
    MockEngine.MockedRequest cancelRequest = buildMockRequest(ENDPOINT1, CANCEL_URL, null, buildDefaultResponse());
    mockEngine.registerRequests(failingStartRequest, cancelRequest);

    ignoreException(() -> decisionTreeApi.start(startRequestModel));

    InOrder inOrder = buildInOrder();
    verifyServiceSetup(newArrayList(ENDPOINT1, ENDPOINT2), inOrder);
    verifyRestTemplateCalledOn(ENDPOINT1.getUrl() + START_URL, inOrder);
    verifyRestTemplateCalledOn(ENDPOINT1.getUrl() + CANCEL_URL, inOrder);
    inOrder.verifyNoMoreInteractions();
    assertOnEngineState(WAITING, WAITING);
  }

  @Test
  public void should_book_all_engines_on_start() {
    ClassifierStartRequestModel startRequestModel = buildClassifierStartRequestModel();
    MockEngine.MockedRequest startRequest1 = buildMockRequest(ENDPOINT1, START_URL, buildClassifierStartRequest(), buildDefaultResponse());
    MockEngine.MockedRequest startRequest2 = buildMockRequest(ENDPOINT2, START_URL, buildClassifierStartRequest(), buildDefaultResponse());
    mockEngine.registerRequests(startRequest1, startRequest2);

    try {
      decisionTreeApi.start(startRequestModel);
      decisionTreeApi.start(startRequestModel);
      decisionTreeApi.start(startRequestModel);
      fail("should fail since all engines are booked");
    } catch (Exception exception) {
      assertOnClassifierServiceException(exception, "No available engine to run decision-tree-start, please try again later");
      assertOnEngineState(BOOKED, BOOKED);
    }
  }
}