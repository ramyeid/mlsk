package org.mlsk.service.impl.inttest.timeseries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.service.timeseries.api.TimeSeriesAnalysisApi;
import org.mlsk.api.service.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.service.impl.inttest.AbstractIT;
import org.mlsk.service.impl.inttest.MockEngine;
import org.mlsk.service.impl.timeseries.api.TimeSeriesAnalysisApiImpl;
import org.mlsk.service.impl.timeseries.service.TimeSeriesAnalysisServiceImpl;
import org.mlsk.service.timeseries.TimeSeriesAnalysisService;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;

import java.math.BigDecimal;

import static com.google.common.collect.Lists.newArrayList;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mlsk.service.impl.inttest.MockEngine.MockedRequest.buildFailingMockRequest;
import static org.mlsk.service.impl.inttest.MockEngine.MockedRequest.buildMockRequest;
import static org.mlsk.service.impl.inttest.timeseries.helper.TimeSeriesAnalysisHelper.*;
import static org.mlsk.service.impl.testhelper.ResponseEntityHelper.assertOnResponseEntity;
import static org.mlsk.service.model.engine.EngineState.WAITING;
import static org.mlsk.service.model.timeseries.utils.TimeSeriesAnalysisConstants.FORECAST_ACCURACY_URL;

@ExtendWith(MockitoExtension.class)
public class TimeSeriesAnalysisForecastAccuracyIT extends AbstractIT {

  private TimeSeriesAnalysisApi timeSeriesAnalysisApi;

  @BeforeEach
  public void setUp() throws Exception {
    super.setup(newArrayList(ENDPOINT1, ENDPOINT2));
    TimeSeriesAnalysisService service = new TimeSeriesAnalysisServiceImpl(orchestrator);
    timeSeriesAnalysisApi = new TimeSeriesAnalysisApiImpl(service);
  }

  // --------------------------------------------
  //                  ACCURACY
  //---------------------------------------------

  @Test
  public void should_return_accuracy_result_from_engine_on_compute_forecast_accuracy() {
    long requestId = 1L;
    TimeSeriesAnalysisRequestModel requestModel = buildServiceTimeSeriesAnalysisRequestModel();
    MockEngine.MockedRequest accuracyMockedRequest = buildMockRequest(ENDPOINT1, FORECAST_ACCURACY_URL, buildEngineTimeSeriesAnalysisRequestModel(requestId), valueOf(2.0));
    mockEngine.registerRequests(accuracyMockedRequest);

    ResponseEntity<BigDecimal> actualResponse = timeSeriesAnalysisApi.computeForecastAccuracy(requestModel);

    assertOnResponseEntity(valueOf(2.0), actualResponse);
    assertOnEngineState(WAITING, WAITING);
  }

  @Test
  public void should_throw_exception_if_engine_returns_an_exception_on_compute_accuracy() {
    long requestId = 1L;
    TimeSeriesAnalysisRequestModel requestModel = buildServiceTimeSeriesAnalysisRequestModel();
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception NPE raised while computing accuracy: NullPointer");
    MockEngine.MockedRequest accuracyMockedRequest = buildFailingMockRequest(ENDPOINT1, FORECAST_ACCURACY_URL, buildEngineTimeSeriesAnalysisRequestModel(requestId), exceptionToThrow);
    mockEngine.registerRequests(accuracyMockedRequest);

    try {
      timeSeriesAnalysisApi.computeForecastAccuracy(requestModel);
      fail("should fail since engine threw exception on compute accuracy");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceException(exception, "Failed on post computeForecastAccuracy to engine: Exception NPE raised while computing accuracy: NullPointer");
      assertOnEngineState(WAITING, WAITING);
    }
  }
}
