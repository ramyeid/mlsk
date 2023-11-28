package org.mlsk.service.impl.inttest.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.service.admin.api.AdminApi;
import org.mlsk.api.service.admin.model.EnginesDetailResponseModel;
import org.mlsk.api.service.timeseries.api.TimeSeriesAnalysisApi;
import org.mlsk.api.service.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.service.admin.AdminService;
import org.mlsk.service.impl.admin.api.AdminApiImpl;
import org.mlsk.service.impl.admin.service.AdminServiceImpl;
import org.mlsk.service.impl.inttest.AbstractIT;
import org.mlsk.service.impl.inttest.MockEngine;
import org.mlsk.service.impl.timeseries.api.TimeSeriesAnalysisApiImpl;
import org.mlsk.service.impl.timeseries.service.TimeSeriesAnalysisServiceImpl;
import org.mlsk.service.timeseries.TimeSeriesAnalysisService;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mlsk.service.impl.inttest.MockEngine.MockedRequest.buildHangingMockRequest;
import static org.mlsk.service.impl.inttest.MockEngine.MockedRequest.buildMockRequest;
import static org.mlsk.service.impl.inttest.admin.helper.AdminHelper.*;
import static org.mlsk.service.impl.inttest.timeseries.helper.TimeSeriesAnalysisHelper.*;
import static org.mlsk.service.impl.testhelper.ResponseEntityHelper.assertOnResponseEntity;
import static org.mlsk.service.model.admin.utils.AdminConstants.PING_URL;
import static org.mlsk.service.model.engine.EngineState.COMPUTING;
import static org.mlsk.service.model.timeseries.utils.TimeSeriesAnalysisConstants.FORECAST_URL;

@ExtendWith(MockitoExtension.class)
public class AdminIT extends AbstractIT {

  private AdminApi adminApi;
  private TimeSeriesAnalysisApi timeSeriesAnalysisApi;

  @BeforeEach
  public void setUp() throws Exception {
    super.setup(newArrayList(ENDPOINT1, ENDPOINT2));
    AdminService adminService = new AdminServiceImpl(orchestrator);
    TimeSeriesAnalysisService timeSeriesAnalysisService = new TimeSeriesAnalysisServiceImpl(orchestrator);
    adminApi = new AdminApiImpl(adminService);
    timeSeriesAnalysisApi = new TimeSeriesAnalysisApiImpl(timeSeriesAnalysisService);
  }

  @Test
  public void should_return_engine_details_from_engine_on_ping_even_if_engine_is_booked() throws InterruptedException {
    long requestId1 = 1L;
    long requestId2 = 2L;
    mockEngine.setupWaitUntilEngineCall();
    TimeSeriesAnalysisRequestModel forecastRequestModel = buildServiceTimeSeriesAnalysisRequestModel();
    MockEngine.MockedRequest forecastMockedRequest1 = buildHangingMockRequest(ENDPOINT1, FORECAST_URL, buildEngineTimeSeriesAnalysisRequestModel(requestId1), buildEngineTimeSeriesResultModel());
    MockEngine.MockedRequest forecastMockedRequest2 = buildHangingMockRequest(ENDPOINT2, FORECAST_URL, buildEngineTimeSeriesAnalysisRequestModel(requestId2), buildEngineTimeSeriesResultModel());
    MockEngine.MockedRequest pingMockedRequest1 = buildMockRequest(ENDPOINT1, PING_URL, null, buildEngine1DetailEngineResponse());
    MockEngine.MockedRequest pingMockedRequest2 = buildMockRequest(ENDPOINT2, PING_URL, null, buildEngine2DetailEngineResponse());
    mockEngine.registerRequests(forecastMockedRequest1, forecastMockedRequest2, pingMockedRequest1, pingMockedRequest2);

    async(() -> timeSeriesAnalysisApi.forecast(forecastRequestModel));
    mockEngine.waitUntilEngineCall();
    async(() -> timeSeriesAnalysisApi.forecast(forecastRequestModel));
    mockEngine.waitUntilEngineCall();
    ResponseEntity<EnginesDetailResponseModel> actualEngineDetail1 = adminApi.ping(of(0));
    ResponseEntity<EnginesDetailResponseModel> actualEngineDetail2 = adminApi.ping(of(1));

    assertOnResponseEntity(new EnginesDetailResponseModel(newArrayList(buildEngine1DetailServiceResponse())), actualEngineDetail1);
    assertOnResponseEntity(new EnginesDetailResponseModel(newArrayList(buildEngine2DetailServiceResponse())), actualEngineDetail2);
    assertOnEngineState(COMPUTING, COMPUTING);
  }

  @Test
  public void should_return_engine_details_from_engine_on_ping_all_even_if_engines_are_booked() throws InterruptedException {
    long requestId1 = 1L;
    long requestId2 = 2L;
    mockEngine.setupWaitUntilEngineCall();
    TimeSeriesAnalysisRequestModel forecastRequestModel = buildServiceTimeSeriesAnalysisRequestModel();
    MockEngine.MockedRequest forecastMockedRequest1 = buildHangingMockRequest(ENDPOINT1, FORECAST_URL, buildEngineTimeSeriesAnalysisRequestModel(requestId1), buildEngineTimeSeriesResultModel());
    MockEngine.MockedRequest forecastMockedRequest2 = buildHangingMockRequest(ENDPOINT2, FORECAST_URL, buildEngineTimeSeriesAnalysisRequestModel(requestId2), buildEngineTimeSeriesResultModel());
    MockEngine.MockedRequest pingMockedRequest1 = buildMockRequest(ENDPOINT1, PING_URL, null, buildEngine1DetailEngineResponse());
    MockEngine.MockedRequest pingMockedRequest2 = buildMockRequest(ENDPOINT2, PING_URL, null, buildEngine2DetailEngineResponse());
    mockEngine.registerRequests(forecastMockedRequest1, forecastMockedRequest2, pingMockedRequest1, pingMockedRequest2);

    async(() -> timeSeriesAnalysisApi.forecast(forecastRequestModel));
    mockEngine.waitUntilEngineCall();
    async(() -> timeSeriesAnalysisApi.forecast(forecastRequestModel));
    mockEngine.waitUntilEngineCall();
    ResponseEntity<EnginesDetailResponseModel> actualEnginesDetails = adminApi.ping(empty());

    assertOnResponseEntity(new EnginesDetailResponseModel(newArrayList(buildEngine1DetailServiceResponse(), buildEngine2DetailServiceResponse())), actualEnginesDetails);
    assertOnEngineState(COMPUTING, COMPUTING);
  }
}
