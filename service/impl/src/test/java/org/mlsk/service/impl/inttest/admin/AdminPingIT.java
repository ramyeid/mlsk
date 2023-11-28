package org.mlsk.service.impl.inttest.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.service.admin.api.AdminApi;
import org.mlsk.api.service.admin.model.EnginesDetailResponseModel;
import org.mlsk.service.admin.AdminService;
import org.mlsk.service.impl.admin.api.AdminApiImpl;
import org.mlsk.service.impl.admin.service.AdminServiceImpl;
import org.mlsk.service.impl.inttest.AbstractIT;
import org.mlsk.service.impl.inttest.MockEngine;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mlsk.service.impl.inttest.MockEngine.MockedRequest.buildFailingMockRequest;
import static org.mlsk.service.impl.inttest.MockEngine.MockedRequest.buildMockRequest;
import static org.mlsk.service.impl.inttest.admin.helper.AdminHelper.*;
import static org.mlsk.service.impl.testhelper.ResponseEntityHelper.assertOnResponseEntity;
import static org.mlsk.service.model.admin.utils.AdminConstants.PING_URL;
import static org.mlsk.service.model.engine.EngineState.IDLE;

@ExtendWith(MockitoExtension.class)
public class AdminPingIT extends AbstractIT {

  private AdminApi adminApi;

  @BeforeEach
  public void setUp() throws Exception {
    super.setup(newArrayList(ENDPOINT1, ENDPOINT2));
    AdminService service = new AdminServiceImpl(orchestrator);
    adminApi = new AdminApiImpl(service);
  }

  @Test
  public void should_return_engine_details_from_engine_on_ping() {
    MockEngine.MockedRequest pingMockedRequest = buildMockRequest(ENDPOINT1, PING_URL, null, buildEngine1DetailEngineResponse());
    mockEngine.registerRequests(pingMockedRequest);

    ResponseEntity<EnginesDetailResponseModel> actualResponse = adminApi.ping(of(0));

    assertOnResponseEntity(new EnginesDetailResponseModel(newArrayList(buildEngine1DetailServiceResponse())), actualResponse);
    assertOnEngineState(IDLE, IDLE);
  }

  @Test
  public void should_throw_exception_if_engine_returns_an_exception_on_ping() {
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.BAD_REQUEST, "Exception NPE raised while pinging: NullPointer");
    MockEngine.MockedRequest pingMockedRequest = buildFailingMockRequest(ENDPOINT1, PING_URL, null, exceptionToThrow);
    mockEngine.registerRequests(pingMockedRequest);

    try {
      adminApi.ping(of(0));
      fail("should fail since engine threw exception on ping");

    } catch (Exception exception) {
      assertOnAdminServiceException(exception, "Failed to call ping to engine: Exception NPE raised while pinging: NullPointer");
      assertOnEngineState(IDLE, IDLE);
    }
  }

  @Test
  public void should_return_engine_details_from_engine_on_ping_all() {
    MockEngine.MockedRequest pingMockedRequest1 = buildMockRequest(ENDPOINT1, PING_URL, null, buildEngine1DetailEngineResponse());
    MockEngine.MockedRequest pingMockedRequest2 = buildMockRequest(ENDPOINT2, PING_URL, null, buildEngine2DetailEngineResponse());
    mockEngine.registerRequests(pingMockedRequest1, pingMockedRequest2);

    ResponseEntity<EnginesDetailResponseModel> actualResponse = adminApi.ping(empty());

    assertOnResponseEntity(new EnginesDetailResponseModel(newArrayList(buildEngine1DetailServiceResponse(), buildEngine2DetailServiceResponse())), actualResponse);
    assertOnEngineState(IDLE, IDLE);
  }

  @Test
  public void should_throw_exception_if_engine_returns_an_exception_on_ping_all() {
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.BAD_REQUEST, "Exception NPE raised while pinging: NullPointer");
    MockEngine.MockedRequest pingMockedRequest1 = buildMockRequest(ENDPOINT1, PING_URL, null, buildEngine1DetailEngineResponse());
    MockEngine.MockedRequest pingMockedRequest2 = buildFailingMockRequest(ENDPOINT2, PING_URL, null, exceptionToThrow);
    mockEngine.registerRequests(pingMockedRequest1, pingMockedRequest2);

    try {
      adminApi.ping(empty());
      fail("should fail since engine threw exception on pingAll");

    } catch (Exception exception) {
      assertOnAdminServiceException(exception, "Failed to call ping to engine: Exception NPE raised while pinging: NullPointer");
      assertOnEngineState(IDLE, IDLE);
    }
  }
}
