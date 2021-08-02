package org.mlsk.service.impl.inttest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.service.impl.Orchestrator;
import org.mlsk.service.impl.controllers.timeseries.TimeSeriesAnalysisController;
import org.mlsk.service.impl.exceptions.TimeSeriesAnalysisServiceException;
import org.mlsk.service.impl.inttest.MockEngine.MockedRequest;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.model.timeseries.TimeSeriesRow;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.junit.jupiter.api.Assertions.*;
import static org.mlsk.service.impl.inttest.MockEngine.MockedRequest.*;
import static org.mlsk.service.model.EngineState.WAITING;
import static org.mlsk.service.utils.TimeSeriesAnalysisUrls.*;

@ExtendWith(MockitoExtension.class)
public class TimeSeriesAnalysisIT extends AbstractIT {

  private TimeSeriesAnalysisController timeSeriesAnalysisController;

  @BeforeEach
  public void setUp() throws IOException {
    super.setup();
    Orchestrator orchestrator = buildOrchestrator(newArrayList(SERVICE_INFO1, SERVICE_INFO2));
    timeSeriesAnalysisController = new TimeSeriesAnalysisController(orchestrator);
  }

  // --------------------------------------------
  //                  FORECAST
  //---------------------------------------------

  @Test
  public void should_return_time_series_result_from_engine_on_forecast() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    MockedRequest forecastMockedRequest = buildMockRequest(SERVICE_INFO1, FORECAST_URL, request, buildTimeSeriesResult());
    mockEngine.registerRequests(forecastMockedRequest);

    TimeSeries actualTimeSeries = timeSeriesAnalysisController.forecast(request);

    assertEquals(buildTimeSeriesResult(), actualTimeSeries);
  }

  @Test
  public void should_throw_exception_if_engine_returns_an_exception_on_forecast() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception NPE raised while forecasting: NullPointer");
    MockedRequest forecastMockedRequest = buildFailingMockRequest(SERVICE_INFO1, FORECAST_URL, request, exceptionToThrow);
    mockEngine.registerRequests(forecastMockedRequest);

    try {
      timeSeriesAnalysisController.forecast(request);
      fail("should fail since engine threw exception on forecast");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceException.class, exception);
      assertEquals("Failed on post forecast to engine: Exception NPE raised while forecasting: NullPointer", exception.getMessage());
    }
  }

  // --------------------------------------------
  //                  PREDICT
  //---------------------------------------------

  @Test
  public void should_return_time_series_result_from_engine_on_predict() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    MockedRequest predictMockedRequest = buildMockRequest(SERVICE_INFO1, PREDICATE_URL, request, buildTimeSeriesResult2());
    mockEngine.registerRequests(predictMockedRequest);

    TimeSeries actualTimeSeries = timeSeriesAnalysisController.predict(request);

    assertEquals(buildTimeSeriesResult2(), actualTimeSeries);
  }

  @Test
  public void should_throw_exception_if_engine_returns_an_exception_on_predict() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception NPE raised while predicting: NullPointer");
    MockedRequest predictMockedRequest = buildFailingMockRequest(SERVICE_INFO1, PREDICATE_URL, request, exceptionToThrow);
    mockEngine.registerRequests(predictMockedRequest);

    try {
      timeSeriesAnalysisController.predict(request);
      fail("should fail since engine threw exception on predict");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceException.class, exception);
      assertEquals("Failed on post predict to engine: Exception NPE raised while predicting: NullPointer", exception.getMessage());
    }
  }

  // --------------------------------------------
  //                  ACCURACY
  //---------------------------------------------

  @Test
  public void should_return_accuracy_result_from_engine_on_compute_forecast_accuracy() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    MockedRequest accuracyMockedRequest = buildMockRequest(SERVICE_INFO1, FORECAST_ACCURACY_URL, request, 2.0);
    mockEngine.registerRequests(accuracyMockedRequest);

    Double actualAccuracy = timeSeriesAnalysisController.computeForecastAccuracy(request);

    assertEquals(2.0, actualAccuracy);
  }

  @Test
  public void should_throw_exception_if_engine_returns_an_exception_on_compute_accuracy() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception NPE raised while computing accuracy: NullPointer");
    MockedRequest accuracyMockedRequest = buildFailingMockRequest(SERVICE_INFO1, FORECAST_ACCURACY_URL, request, exceptionToThrow);
    mockEngine.registerRequests(accuracyMockedRequest);

    try {
      timeSeriesAnalysisController.computeForecastAccuracy(request);
      fail("should fail since engine threw exception on compute accuracy");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceException.class, exception);
      assertEquals("Failed on post computeForecastAccuracy to engine: Exception NPE raised while computing accuracy: NullPointer", exception.getMessage());
    }
  }

  // --------------------------------------------
  //                  FORECAST VS ACTUAL
  //---------------------------------------------

  @Test
  public void should_return_time_series_from_engine_on_forecast_vs_actual() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    TimeSeriesAnalysisRequest expectedRequest = buildTimeSeriesAnalysisExpectedRequestForecastVsActual();
    MockedRequest forecastVsActualMockedRequest = buildMockRequest(SERVICE_INFO1, FORECAST_URL, expectedRequest, buildTimeSeriesResult());
    mockEngine.registerRequests(forecastVsActualMockedRequest);

    TimeSeries actualTimeSeries = timeSeriesAnalysisController.forecastVsActual(request);

    assertEquals(buildTimeSeriesResult(), actualTimeSeries);
  }

  @Test
  public void should_throw_exception_if_engine_returns_an_exception_on_forecast_vs_actual() {
    TimeSeriesAnalysisRequest request = buildTimeSeriesAnalysisRequest();
    TimeSeriesAnalysisRequest expectedRequest = buildTimeSeriesAnalysisExpectedRequestForecastVsActual();
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception NPE raised while computing forecast: NullPointer");
    MockedRequest forecastVsActualMockedRequest = buildFailingMockRequest(SERVICE_INFO1, FORECAST_URL, expectedRequest, exceptionToThrow);
    mockEngine.registerRequests(forecastVsActualMockedRequest);

    try {
      timeSeriesAnalysisController.forecastVsActual(request);
      fail("should fail since engine threw exception on compute forecast vs actual");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceException.class, exception);
      assertEquals("Failed on post forecast to engine: Exception NPE raised while computing forecast: NullPointer", exception.getMessage());
    }
  }

  // --------------------------------------------
  //                  MULTIPLE REQUESTS
  //---------------------------------------------

  @Test
  public void should_return_results_from_requests_in_parallel_on_engines() throws Exception {
    mockEngine.setupWaitUntilEngineCall();
    TimeSeriesAnalysisRequest forecastRequest = buildTimeSeriesAnalysisRequest();
    TimeSeriesAnalysisRequest predictRequest = buildTimeSeriesAnalysisRequest();
    MockedRequest forecastMockedRequest = buildHangingMockRequest(SERVICE_INFO1, FORECAST_URL, forecastRequest, buildTimeSeriesResult());
    MockedRequest predicateMockedRequest = buildMockRequest(SERVICE_INFO2, PREDICATE_URL, predictRequest, buildTimeSeriesResult2());
    mockEngine.registerRequests(forecastMockedRequest, predicateMockedRequest);

    CompletableFuture<TimeSeries> actualForecastFuture = supplyAsync(() -> timeSeriesAnalysisController.forecast(forecastRequest));
    mockEngine.waitUntilEngineCall();
    TimeSeries actualPredict = timeSeriesAnalysisController.predict(predictRequest);
    forecastMockedRequest.countDownLatch.countDown();
    TimeSeries actualForecast = actualForecastFuture.join();

    assertEquals(buildTimeSeriesResult(), actualForecast);
    assertEquals(buildTimeSeriesResult2(), actualPredict);
    assertEquals(WAITING, engines.get(0).getState());
    assertEquals(WAITING, engines.get(1).getState());
  }

  @Test
  public void should_handle_requests_in_parallel_on_engines() throws Exception {
    mockEngine.setupWaitUntilEngineCall();
    TimeSeriesAnalysisRequest forecastRequest = buildTimeSeriesAnalysisRequest();
    TimeSeriesAnalysisRequest predictRequest = buildTimeSeriesAnalysisRequest();
    MockedRequest forecastMockedRequest = buildHangingMockRequest(SERVICE_INFO1, FORECAST_URL, forecastRequest, buildTimeSeriesResult());
    MockedRequest predicateMockedRequest = buildMockRequest(SERVICE_INFO2, PREDICATE_URL, predictRequest, buildTimeSeriesResult2());
    mockEngine.registerRequests(forecastMockedRequest, predicateMockedRequest);

    CompletableFuture<TimeSeries> actualForecastFuture = supplyAsync(() -> timeSeriesAnalysisController.forecast(forecastRequest));
    mockEngine.waitUntilEngineCall();
    timeSeriesAnalysisController.predict(predictRequest);
    forecastMockedRequest.countDownLatch.countDown();
    actualForecastFuture.join();

    InOrder inOrder = buildInOrder();
    verifyServiceSetup(newArrayList(SERVICE_INFO1, SERVICE_INFO2), inOrder);
    verifyRestTemplateCalledOn(SERVICE_INFO1.getUrl() + FORECAST_URL, inOrder);
    verifyRestTemplateCalledOn(SERVICE_INFO2.getUrl() + PREDICATE_URL, inOrder);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_exception_if_engines_are_busy_and_new_request_received() {
    mockEngine.setupWaitUntilEngineCall();
    TimeSeriesAnalysisRequest forecastRequest = buildTimeSeriesAnalysisRequest();
    TimeSeriesAnalysisRequest predictRequest = buildTimeSeriesAnalysisRequest();
    MockedRequest forecastMockedRequest = buildHangingMockRequest(SERVICE_INFO1, FORECAST_URL, forecastRequest, buildTimeSeriesResult());
    MockedRequest forecastAccuracyMockedRequest = buildHangingMockRequest(SERVICE_INFO2, FORECAST_ACCURACY_URL, predictRequest, 2.);
    mockEngine.registerRequests(forecastMockedRequest, forecastAccuracyMockedRequest);

    try {
      ExecutorService executor = Executors.newFixedThreadPool(2);
      supplyAsync(() -> timeSeriesAnalysisController.forecast(forecastRequest), executor);
      mockEngine.waitUntilEngineCall();
      supplyAsync(() -> timeSeriesAnalysisController.computeForecastAccuracy(forecastRequest), executor);
      mockEngine.waitUntilEngineCall();
      timeSeriesAnalysisController.predict(predictRequest);
      fail("should fail since three requests sent and two engines are busy");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisServiceException.class, exception);
      assertEquals("No available engine to run time-series-predict please try again later", exception.getMessage());
    }
  }

  private static TimeSeries buildTimeSeriesResult() {
    TimeSeriesRow row1 = new TimeSeriesRow("date1", 1.);
    TimeSeriesRow row2 = new TimeSeriesRow("date2", 2.);

    List<TimeSeriesRow> rows = newArrayList(row1, row2);
    return new TimeSeries(rows, "dateColumnName", "valueColumnName", "yyyy-MM");
  }

  private static TimeSeries buildTimeSeriesResult2() {
    TimeSeriesRow row1 = new TimeSeriesRow("date3", 3.);
    TimeSeriesRow row2 = new TimeSeriesRow("date4", 4.);

    List<TimeSeriesRow> rows = newArrayList(row1, row2);
    return new TimeSeries(rows, "date", "value", "yyyy");
  }

  private static TimeSeries buildTimeSeriesRequest() {
    TimeSeriesRow row1 = new TimeSeriesRow("date-1", 11.);
    TimeSeriesRow row2 = new TimeSeriesRow("date0", 22.);
    TimeSeriesRow row3 = new TimeSeriesRow("date1", 33.);
    TimeSeriesRow row4 = new TimeSeriesRow("date2", 44.);

    List<TimeSeriesRow> rows = newArrayList(row1, row2, row3, row4);
    return new TimeSeries(rows, "dateColumnName", "valueColumnName", "yyyy-MM");
  }

  private static TimeSeriesAnalysisRequest buildTimeSeriesAnalysisRequest() {
    return new TimeSeriesAnalysisRequest(buildTimeSeriesRequest(), 2);
  }

  private static TimeSeriesAnalysisRequest buildTimeSeriesAnalysisExpectedRequestForecastVsActual() {
    TimeSeriesRow row1 = new TimeSeriesRow("date-1", 11.);
    TimeSeriesRow row2 = new TimeSeriesRow("date0", 22.);

    List<TimeSeriesRow> rows = newArrayList(row1, row2);
    TimeSeries timeSeries = new TimeSeries(rows, "dateColumnName", "valueColumnName", "yyyy-MM");

    return new TimeSeriesAnalysisRequest(timeSeries, 2);
  }

}
