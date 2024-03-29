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
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static com.google.common.collect.Lists.newArrayList;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mlsk.service.impl.inttest.MockEngine.MockedRequest.*;
import static org.mlsk.service.impl.inttest.timeseries.helper.TimeSeriesAnalysisHelper.*;
import static org.mlsk.service.impl.testhelper.ResponseEntityHelper.assertOnResponseEntity;
import static org.mlsk.service.model.engine.EngineState.COMPUTING;
import static org.mlsk.service.model.engine.EngineState.IDLE;
import static org.mlsk.service.model.timeseries.utils.TimeSeriesAnalysisConstants.*;

@ExtendWith(MockitoExtension.class)
public class TimeSeriesAnalysisIT extends AbstractIT {

  private TimeSeriesAnalysisApi timeSeriesAnalysisApi;

  @BeforeEach
  public void setUp() throws Exception {
    super.setup(newArrayList(ENDPOINT1, ENDPOINT2));
    TimeSeriesAnalysisService service = new TimeSeriesAnalysisServiceImpl(orchestrator);
    timeSeriesAnalysisApi = new TimeSeriesAnalysisApiImpl(service);
  }

  // --------------------------------------------
  //                  MULTIPLE REQUESTS
  //---------------------------------------------

  @Test
  public void should_return_results_from_requests_in_parallel_on_engines() throws Exception {
    long requestId1 = 1L;
    long requestId2 = 2L;
    mockEngine.setupWaitUntilEngineCall();
    TimeSeriesAnalysisRequestModel forecastRequestModel = buildServiceTimeSeriesAnalysisRequestModel();
    TimeSeriesAnalysisRequestModel predictRequestModel = buildServiceTimeSeriesAnalysisRequestModel();
    MockedRequest forecastMockedRequest = buildHangingMockRequest(ENDPOINT1, FORECAST_URL, buildEngineTimeSeriesAnalysisRequestModel(requestId1), buildEngineTimeSeriesResultModel());
    MockedRequest predictMockedRequest = buildMockRequest(ENDPOINT2, PREDICT_URL, buildEngineTimeSeriesAnalysisRequestModel(requestId2), buildEngineTimeSeriesResult2Model());
    mockEngine.registerRequests(forecastMockedRequest, predictMockedRequest);

    CompletableFuture<ResponseEntity<TimeSeriesModel>> actualForecastFuture = async(() -> timeSeriesAnalysisApi.forecast(forecastRequestModel));
    mockEngine.waitUntilEngineCall();
    ResponseEntity<TimeSeriesModel> actualPredict = timeSeriesAnalysisApi.predict(predictRequestModel);
    forecastMockedRequest.releaseLatch();
    ResponseEntity<TimeSeriesModel> actualForecast = actualForecastFuture.join();

    assertOnResponseEntity(buildServiceTimeSeriesModelResultModel(), actualForecast);
    assertOnResponseEntity(buildServiceTimeSeriesModelResult2Model(), actualPredict);
    assertOnEngineState(IDLE, IDLE);
  }

  @Test
  public void should_handle_requests_in_parallel_on_engines() throws Exception {
    long requestId1 = 1L;
    long requestId2 = 2L;
    mockEngine.setupWaitUntilEngineCall();
    TimeSeriesAnalysisRequestModel forecastRequestModel = buildServiceTimeSeriesAnalysisRequestModel();
    TimeSeriesAnalysisRequestModel predictRequestModel = buildServiceTimeSeriesAnalysisRequestModel();
    MockedRequest forecastMockedRequest = buildHangingMockRequest(ENDPOINT1, FORECAST_URL, buildEngineTimeSeriesAnalysisRequestModel(requestId1), buildEngineTimeSeriesResultModel());
    MockedRequest predictMockedRequest = buildMockRequest(ENDPOINT2, PREDICT_URL, buildEngineTimeSeriesAnalysisRequestModel(requestId2), buildEngineTimeSeriesResult2Model());
    mockEngine.registerRequests(forecastMockedRequest, predictMockedRequest);

    CompletableFuture<ResponseEntity<TimeSeriesModel>> actualForecastFuture = async(() -> timeSeriesAnalysisApi.forecast(forecastRequestModel));
    mockEngine.waitUntilEngineCall();
    timeSeriesAnalysisApi.predict(predictRequestModel);
    forecastMockedRequest.releaseLatch();
    actualForecastFuture.join();

    InOrder inOrder = buildInOrder();
    verifyServiceSetup(newArrayList(ENDPOINT1, ENDPOINT2), inOrder);
    verifyRestTemplateCalledOn(ENDPOINT1.getUrl() + FORECAST_URL, inOrder);
    verifyRestTemplateCalledOn(ENDPOINT2.getUrl() + PREDICT_URL, inOrder);
    inOrder.verifyNoMoreInteractions();
    assertOnEngineState(IDLE, IDLE);
  }

  @Test
  public void should_throw_exception_if_engines_are_busy_and_new_request_received() {
    long requestId1 = 1L;
    long requestId2 = 2L;
    mockEngine.setupWaitUntilEngineCall();
    TimeSeriesAnalysisRequestModel forecastRequestModel = buildServiceTimeSeriesAnalysisRequestModel();
    TimeSeriesAnalysisRequestModel predictRequestModel = buildServiceTimeSeriesAnalysisRequestModel();
    MockedRequest forecastMockedRequest = buildHangingMockRequest(ENDPOINT1, FORECAST_URL, buildEngineTimeSeriesAnalysisRequestModel(requestId1), buildEngineTimeSeriesResultModel());
    MockedRequest forecastAccuracyMockedRequest = buildHangingMockRequest(ENDPOINT2, FORECAST_ACCURACY_URL, buildEngineTimeSeriesAnalysisRequestModel(requestId2), valueOf(2.));
    mockEngine.registerRequests(forecastMockedRequest, forecastAccuracyMockedRequest);

    try {
      async(() -> timeSeriesAnalysisApi.forecast(forecastRequestModel));
      mockEngine.waitUntilEngineCall();
      async(() -> timeSeriesAnalysisApi.computeForecastAccuracy(forecastRequestModel));
      mockEngine.waitUntilEngineCall();
      timeSeriesAnalysisApi.predict(predictRequestModel);
      fail("should fail since three requests sent and two engines are busy");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceException(exception, "No available engine to run time-series-predict, please try again later");
      assertOnEngineState(COMPUTING, COMPUTING);
    }
  }

  @Test
  public void should_release_engine_on_failure_and_handle_three_requests_with_one_failure_on_two_engines() throws Exception {
    long requestId1 = 1L;
    long requestId2 = 2L;
    long requestId3 = 3L;
    TimeSeriesAnalysisRequestModel forecastRequestModel = buildServiceTimeSeriesAnalysisRequestModel();
    TimeSeriesAnalysisRequestModel forecastAccuracyRequestModel = buildServiceTimeSeriesAnalysisRequestModel();
    TimeSeriesAnalysisRequestModel forecastVsActualRequestModel = buildServiceTimeSeriesAnalysisRequestModel();
    MockedRequest forecastMockedRequest = buildFailingMockRequest(ENDPOINT1, FORECAST_URL, buildEngineTimeSeriesAnalysisRequestModel(requestId1), new RuntimeException("Exception On Forecast"));
    MockedRequest forecastAccuracyMockedRequest = buildHangingMockRequest(ENDPOINT1, FORECAST_ACCURACY_URL, buildEngineTimeSeriesAnalysisRequestModel(requestId2), valueOf(2.));
    MockedRequest forecastVsActualMockedRequest = buildHangingMockRequest(ENDPOINT2, FORECAST_URL, buildEngineTimeSeriesAnalysisExpectedRequestForecastVsActualModel(requestId3), buildEngineTimeSeriesResultModel());
    mockEngine.registerRequests(forecastMockedRequest, forecastAccuracyMockedRequest, forecastVsActualMockedRequest);

    ignoreException(() -> timeSeriesAnalysisApi.forecast(forecastRequestModel));
    mockEngine.setupWaitUntilEngineCall();
    CompletableFuture<ResponseEntity<BigDecimal>> actualForecastAccuracyFuture = async(() -> timeSeriesAnalysisApi.computeForecastAccuracy(forecastAccuracyRequestModel));
    mockEngine.waitUntilEngineCall();
    CompletableFuture<ResponseEntity<TimeSeriesModel>> actualForecastVsActualFuture = async(() -> timeSeriesAnalysisApi.forecastVsActual(forecastVsActualRequestModel));
    mockEngine.waitUntilEngineCall();
    forecastAccuracyMockedRequest.releaseLatch();
    forecastVsActualMockedRequest.releaseLatch();
    actualForecastAccuracyFuture.join();
    actualForecastVsActualFuture.join();

    assertOnResponseEntity(valueOf(2.), actualForecastAccuracyFuture.get());
    assertOnResponseEntity(buildServiceTimeSeriesModelResultModel(), actualForecastVsActualFuture.get());
    assertOnEngineState(IDLE, IDLE);
  }
}
