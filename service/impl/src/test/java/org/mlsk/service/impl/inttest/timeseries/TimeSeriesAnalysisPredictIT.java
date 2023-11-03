package org.mlsk.service.impl.inttest.timeseries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.timeseries.api.TimeSeriesAnalysisApi;
import org.mlsk.api.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.timeseries.model.TimeSeriesModel;
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
import static org.mlsk.service.timeseries.utils.TimeSeriesAnalysisConstants.PREDICT_URL;

@ExtendWith(MockitoExtension.class)
public class TimeSeriesAnalysisPredictIT extends AbstractIT {

  private TimeSeriesAnalysisApi timeSeriesAnalysisApi;

  @BeforeEach
  public void setUp() throws Exception {
    super.setup(newArrayList(ENDPOINT1, ENDPOINT2));
    TimeSeriesAnalysisService service = new TimeSeriesAnalysisServiceImpl(orchestrator);
    timeSeriesAnalysisApi = new TimeSeriesAnalysisApiImpl(service);
  }

  // --------------------------------------------
  //                  PREDICT
  //---------------------------------------------

  @Test
  public void should_return_time_series_result_from_engine_on_predict() {
    TimeSeriesAnalysisRequestModel requestModel = buildTimeSeriesAnalysisRequestModel();
    MockEngine.MockedRequest predictMockedRequest = buildMockRequest(ENDPOINT1, PREDICT_URL, buildTimeSeriesAnalysisRequest(), buildTimeSeriesResult2());
    mockEngine.registerRequests(predictMockedRequest);

    ResponseEntity<TimeSeriesModel> actualResponse = timeSeriesAnalysisApi.predict(requestModel);

    assertOnResponseEntity(buildTimeSeriesModelResult2(), actualResponse);
    assertOnEngineState(WAITING, WAITING);
  }

  @Test
  public void should_throw_exception_if_engine_returns_an_exception_on_predict() {
    TimeSeriesAnalysisRequestModel requestModel = buildTimeSeriesAnalysisRequestModel();
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception NPE raised while predicting: NullPointer");
    MockEngine.MockedRequest predictMockedRequest = buildFailingMockRequest(ENDPOINT1, PREDICT_URL, buildTimeSeriesAnalysisRequest(), exceptionToThrow);
    mockEngine.registerRequests(predictMockedRequest);

    try {
      timeSeriesAnalysisApi.predict(requestModel);
      fail("should fail since engine threw exception on predict");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceException(exception, "Failed on post predict to engine: Exception NPE raised while predicting: NullPointer");
      assertOnEngineState(WAITING, WAITING);
    }
  }
}
