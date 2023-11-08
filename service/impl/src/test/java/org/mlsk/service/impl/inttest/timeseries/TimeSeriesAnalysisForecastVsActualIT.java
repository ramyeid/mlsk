package org.mlsk.service.impl.inttest.timeseries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.service.timeseries.api.TimeSeriesAnalysisApi;
import org.mlsk.api.service.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.service.timeseries.model.TimeSeriesModel;
import org.mlsk.service.impl.inttest.AbstractIT;
import org.mlsk.service.impl.inttest.MockEngine;
import org.mlsk.service.impl.timeseries.api.TimeSeriesAnalysisApiImpl;
import org.mlsk.service.impl.timeseries.service.TimeSeriesAnalysisServiceImpl;
import org.mlsk.service.timeseries.TimeSeriesAnalysisService;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mlsk.service.impl.inttest.MockEngine.MockedRequest.buildFailingMockRequest;
import static org.mlsk.service.impl.inttest.MockEngine.MockedRequest.buildMockRequest;
import static org.mlsk.service.impl.inttest.timeseries.helper.TimeSeriesAnalysisHelper.*;
import static org.mlsk.service.impl.testhelper.ResponseEntityHelper.assertOnResponseEntity;
import static org.mlsk.service.model.engine.EngineState.WAITING;
import static org.mlsk.service.timeseries.utils.TimeSeriesAnalysisConstants.FORECAST_URL;

@ExtendWith(MockitoExtension.class)
public class TimeSeriesAnalysisForecastVsActualIT extends AbstractIT {

  private TimeSeriesAnalysisApi timeSeriesAnalysisApi;

  @BeforeEach
  public void setUp() throws Exception {
    super.setup(newArrayList(ENDPOINT1, ENDPOINT2));
    TimeSeriesAnalysisService service = new TimeSeriesAnalysisServiceImpl(orchestrator);
    timeSeriesAnalysisApi = new TimeSeriesAnalysisApiImpl(service);
  }

  // --------------------------------------------
  //                  FORECAST VS ACTUAL
  //---------------------------------------------

  @Test
  public void should_return_time_series_from_engine_on_forecast_vs_actual() {
    long requestId = 1L;
    TimeSeriesAnalysisRequestModel requestModel = buildServiceTimeSeriesAnalysisRequestModel();
    MockEngine.MockedRequest forecastVsActualMockedRequest = buildMockRequest(ENDPOINT1, FORECAST_URL, buildEngineTimeSeriesAnalysisExpectedRequestForecastVsActualModel(requestId), buildEngineTimeSeriesResultModel());
    mockEngine.registerRequests(forecastVsActualMockedRequest);

    ResponseEntity<TimeSeriesModel> actualResponse = timeSeriesAnalysisApi.forecastVsActual(requestModel);

    assertOnResponseEntity(buildServiceTimeSeriesModelResultModel(), actualResponse);
    assertOnEngineState(WAITING, WAITING);
  }

  @Test
  public void should_throw_exception_if_engine_returns_an_exception_on_forecast_vs_actual() {
    long requestId = 1L;
    TimeSeriesAnalysisRequestModel requestModel = buildServiceTimeSeriesAnalysisRequestModel();
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception NPE raised while computing forecast: NullPointer");
    MockEngine.MockedRequest forecastVsActualMockedRequest = buildFailingMockRequest(ENDPOINT1, FORECAST_URL, buildEngineTimeSeriesAnalysisExpectedRequestForecastVsActualModel(requestId), exceptionToThrow);
    mockEngine.registerRequests(forecastVsActualMockedRequest);

    try {
      timeSeriesAnalysisApi.forecastVsActual(requestModel);
      fail("should fail since engine threw exception on compute forecast vs actual");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceException(exception, "Failed on post forecast to engine: Exception NPE raised while computing forecast: NullPointer");
      assertOnEngineState(WAITING, WAITING);
    }
  }
}