package org.mlsk.service.impl.inttest.timeseries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.timeseries.api.TimeSeriesAnalysisApi;
import org.mlsk.api.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.timeseries.model.TimeSeriesModel;
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
import static org.mlsk.service.model.engine.EngineState.WAITING;
import static org.mlsk.service.timeseries.utils.TimeSeriesAnalysisConstants.*;

@ExtendWith(MockitoExtension.class)
public class TimeSeriesAnalysisIT extends AbstractIT {

  private TimeSeriesAnalysisApi timeSeriesAnalysisApi;

  @BeforeEach
  public void setUp() throws Exception {
    super.setup(newArrayList(SERVICE_INFO1, SERVICE_INFO2));
    TimeSeriesAnalysisService service = new TimeSeriesAnalysisServiceImpl(orchestrator);
    timeSeriesAnalysisApi = new TimeSeriesAnalysisApiImpl(service);
  }

  // --------------------------------------------
  //                  MULTIPLE REQUESTS
  //---------------------------------------------

  @Test
  public void should_return_results_from_requests_in_parallel_on_engines() throws Exception {
    mockEngine.setupWaitUntilEngineCall();
    TimeSeriesAnalysisRequestModel forecastRequestModel = buildTimeSeriesAnalysisRequestModel();
    TimeSeriesAnalysisRequestModel predictRequestModel = buildTimeSeriesAnalysisRequestModel();
    MockedRequest forecastMockedRequest = buildHangingMockRequest(SERVICE_INFO1, FORECAST_URL, buildTimeSeriesAnalysisRequest(), buildTimeSeriesResult());
    MockedRequest predictMockedRequest = buildMockRequest(SERVICE_INFO2, PREDICT_URL, buildTimeSeriesAnalysisRequest(), buildTimeSeriesResult2());
    mockEngine.registerRequests(forecastMockedRequest, predictMockedRequest);

    CompletableFuture<ResponseEntity<TimeSeriesModel>> actualForecastFuture = async(() -> timeSeriesAnalysisApi.forecast(forecastRequestModel));
    mockEngine.waitUntilEngineCall();
    ResponseEntity<TimeSeriesModel> actualPredict = timeSeriesAnalysisApi.predict(predictRequestModel);
    forecastMockedRequest.releaseLatch();
    ResponseEntity<TimeSeriesModel> actualForecast = actualForecastFuture.join();

    assertOnResponseEntity(buildTimeSeriesModelResult(), actualForecast);
    assertOnResponseEntity(buildTimeSeriesModelResult2(), actualPredict);
    assertOnEngineState(WAITING, WAITING);
  }

  @Test
  public void should_handle_requests_in_parallel_on_engines() throws Exception {
    mockEngine.setupWaitUntilEngineCall();
    TimeSeriesAnalysisRequestModel forecastRequestModel = buildTimeSeriesAnalysisRequestModel();
    TimeSeriesAnalysisRequestModel predictRequestModel = buildTimeSeriesAnalysisRequestModel();
    MockedRequest forecastMockedRequest = buildHangingMockRequest(SERVICE_INFO1, FORECAST_URL, buildTimeSeriesAnalysisRequest(), buildTimeSeriesResult());
    MockedRequest predictMockedRequest = buildMockRequest(SERVICE_INFO2, PREDICT_URL, buildTimeSeriesAnalysisRequest(), buildTimeSeriesResult2());
    mockEngine.registerRequests(forecastMockedRequest, predictMockedRequest);

    CompletableFuture<ResponseEntity<TimeSeriesModel>> actualForecastFuture = async(() -> timeSeriesAnalysisApi.forecast(forecastRequestModel));
    mockEngine.waitUntilEngineCall();
    timeSeriesAnalysisApi.predict(predictRequestModel);
    forecastMockedRequest.releaseLatch();
    actualForecastFuture.join();

    InOrder inOrder = buildInOrder();
    verifyServiceSetup(newArrayList(SERVICE_INFO1, SERVICE_INFO2), inOrder);
    verifyRestTemplateCalledOn(SERVICE_INFO1.getUrl() + FORECAST_URL, inOrder);
    verifyRestTemplateCalledOn(SERVICE_INFO2.getUrl() + PREDICT_URL, inOrder);
    inOrder.verifyNoMoreInteractions();
    assertOnEngineState(WAITING, WAITING);
  }

  @Test
  public void should_throw_exception_if_engines_are_busy_and_new_request_received() {
    mockEngine.setupWaitUntilEngineCall();
    TimeSeriesAnalysisRequestModel forecastRequestModel = buildTimeSeriesAnalysisRequestModel();
    TimeSeriesAnalysisRequestModel predictRequestModel = buildTimeSeriesAnalysisRequestModel();
    MockedRequest forecastMockedRequest = buildHangingMockRequest(SERVICE_INFO1, FORECAST_URL, buildTimeSeriesAnalysisRequest(), buildTimeSeriesResult());
    MockedRequest forecastAccuracyMockedRequest = buildHangingMockRequest(SERVICE_INFO2, FORECAST_ACCURACY_URL, buildTimeSeriesAnalysisRequest(), 2.);
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
    TimeSeriesAnalysisRequestModel forecastRequestModel = buildTimeSeriesAnalysisRequestModel();
    TimeSeriesAnalysisRequestModel forecastAccuracyRequestModel = buildTimeSeriesAnalysisRequestModel();
    TimeSeriesAnalysisRequestModel forecastVsActualRequestModel = buildTimeSeriesAnalysisRequestModel();
    MockedRequest forecastMockedRequest = buildFailingMockRequest(SERVICE_INFO1, FORECAST_URL, buildTimeSeriesAnalysisRequest(), new RuntimeException("Exception On Forecast"));
    MockedRequest forecastAccuracyMockedRequest = buildHangingMockRequest(SERVICE_INFO1, FORECAST_ACCURACY_URL, buildTimeSeriesAnalysisRequest(), 2.);
    MockedRequest forecastVsActualMockedRequest = buildHangingMockRequest(SERVICE_INFO2, FORECAST_URL, buildTimeSeriesAnalysisExpectedRequestForecastVsActual(), buildTimeSeriesResult());
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
    assertOnResponseEntity(buildTimeSeriesModelResult(), actualForecastVsActualFuture.get());
    assertOnEngineState(WAITING, WAITING);
  }
}
