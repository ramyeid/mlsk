package org.mlsk.service.impl.inttest.timeseries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.service.timeseries.api.TimeSeriesAnalysisApi;
import org.mlsk.api.service.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.service.timeseries.model.TimeSeriesModel;
import org.mlsk.service.impl.inttest.AbstractIT;
import org.mlsk.service.impl.inttest.MockEngine.MockedRequest;
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
public class TimeSeriesAnalysisForecastIT extends AbstractIT {

  private TimeSeriesAnalysisApi timeSeriesAnalysisApi;

  @BeforeEach
  public void setUp() throws Exception {
    super.setup(newArrayList(ENDPOINT1, ENDPOINT2));
    TimeSeriesAnalysisService service = new TimeSeriesAnalysisServiceImpl(orchestrator);
    timeSeriesAnalysisApi = new TimeSeriesAnalysisApiImpl(service);
  }

  // --------------------------------------------
  //                  FORECAST
  //---------------------------------------------

  @Test
  public void should_return_time_series_result_from_engine_on_forecast() {
    long requestId = 1L;
    TimeSeriesAnalysisRequestModel requestModel = buildTimeSeriesAnalysisRequestModel();
    MockedRequest forecastMockedRequest = buildMockRequest(ENDPOINT1, FORECAST_URL, buildTimeSeriesAnalysisRequest(requestId), buildTimeSeriesResult());
    mockEngine.registerRequests(forecastMockedRequest);

    ResponseEntity<TimeSeriesModel> actualResponse = timeSeriesAnalysisApi.forecast(requestModel);

    assertOnResponseEntity(buildTimeSeriesModelResult(), actualResponse);
    assertOnEngineState(WAITING, WAITING);
  }

  @Test
  public void should_throw_exception_if_engine_returns_an_exception_on_forecast() {
    long requestId = 1L;
    TimeSeriesAnalysisRequestModel requestModel = buildTimeSeriesAnalysisRequestModel();
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.BAD_REQUEST, "Exception NPE raised while forecasting: NullPointer");
    MockedRequest forecastMockedRequest = buildFailingMockRequest(ENDPOINT1, FORECAST_URL, buildTimeSeriesAnalysisRequest(requestId), exceptionToThrow);
    mockEngine.registerRequests(forecastMockedRequest);

    try {
      timeSeriesAnalysisApi.forecast(requestModel);
      fail("should fail since engine threw exception on forecast");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceException(exception, "Failed on post forecast to engine: Exception NPE raised while forecasting: NullPointer");
      assertOnEngineState(WAITING, WAITING);
    }
  }
}