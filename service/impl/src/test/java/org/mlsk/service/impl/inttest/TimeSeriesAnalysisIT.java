package org.mlsk.service.impl.inttest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.timeseries.api.TimeSeriesAnalysisApi;
import org.mlsk.api.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.timeseries.model.TimeSeriesModel;
import org.mlsk.api.timeseries.model.TimeSeriesRowModel;
import org.mlsk.service.impl.Orchestrator;
import org.mlsk.service.impl.controllers.timeseries.TimeSeriesAnalysisApiImpl;
import org.mlsk.service.impl.exceptions.TimeSeriesAnalysisServiceException;
import org.mlsk.service.impl.inttest.MockEngine.MockedRequest;
import org.mlsk.service.impl.mapper.timeseries.TimeSeriesModelHelper;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.model.timeseries.TimeSeriesRow;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.collect.Lists.newArrayList;
import static java.math.BigDecimal.valueOf;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.junit.jupiter.api.Assertions.*;
import static org.mlsk.service.impl.inttest.MockEngine.MockedRequest.*;
import static org.mlsk.service.impl.mapper.timeseries.TimeSeriesModelHelper.buildTimeSeriesModel;
import static org.mlsk.service.impl.mapper.timeseries.TimeSeriesModelHelper.buildTimeSeriesRowModel;
import static org.mlsk.service.model.EngineState.WAITING;
import static org.mlsk.service.utils.TimeSeriesAnalysisUrls.*;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
public class TimeSeriesAnalysisIT extends AbstractIT {

  private TimeSeriesAnalysisApi timeSeriesAnalysisApi;

  @BeforeEach
  public void setUp() throws IOException {
    super.setup();
    Orchestrator orchestrator = buildOrchestrator(newArrayList(SERVICE_INFO1, SERVICE_INFO2));
    timeSeriesAnalysisApi = new TimeSeriesAnalysisApiImpl(orchestrator);
  }

  // --------------------------------------------
  //                  FORECAST
  //---------------------------------------------

  @Test
  public void should_return_time_series_result_from_engine_on_forecast() {
    TimeSeriesAnalysisRequestModel requestModel = buildTimeSeriesAnalysisRequestModel();
    MockedRequest forecastMockedRequest = buildMockRequest(SERVICE_INFO1, FORECAST_URL, buildTimeSeriesAnalysisRequest(), buildTimeSeriesResult());
    mockEngine.registerRequests(forecastMockedRequest);

    ResponseEntity<TimeSeriesModel> actualResponse = timeSeriesAnalysisApi.forecast(requestModel);

    assertOnResponseEntity(actualResponse, buildTimeSeriesModelResult());
  }

  @Test
  public void should_throw_exception_if_engine_returns_an_exception_on_forecast() {
    TimeSeriesAnalysisRequestModel requestModel = buildTimeSeriesAnalysisRequestModel();
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception NPE raised while forecasting: NullPointer");
    MockedRequest forecastMockedRequest = buildFailingMockRequest(SERVICE_INFO1, FORECAST_URL, buildTimeSeriesAnalysisRequest(), exceptionToThrow);
    mockEngine.registerRequests(forecastMockedRequest);

    try {
      timeSeriesAnalysisApi.forecast(requestModel);
      fail("should fail since engine threw exception on forecast");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceException(exception, "Failed on post forecast to engine: Exception NPE raised while forecasting: NullPointer");
    }
  }

  // --------------------------------------------
  //                  PREDICT
  //---------------------------------------------

  @Test
  public void should_return_time_series_result_from_engine_on_predict() {
    TimeSeriesAnalysisRequestModel requestModel = buildTimeSeriesAnalysisRequestModel();
    MockedRequest predictMockedRequest = buildMockRequest(SERVICE_INFO1, PREDICATE_URL, buildTimeSeriesAnalysisRequest(), buildTimeSeriesResult2());
    mockEngine.registerRequests(predictMockedRequest);

    ResponseEntity<TimeSeriesModel> actualResponse = timeSeriesAnalysisApi.predict(requestModel);

    assertOnResponseEntity(actualResponse, buildTimeSeriesModelResult2());
  }

  @Test
  public void should_throw_exception_if_engine_returns_an_exception_on_predict() {
    TimeSeriesAnalysisRequestModel requestModel = buildTimeSeriesAnalysisRequestModel();
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception NPE raised while predicting: NullPointer");
    MockedRequest predictMockedRequest = buildFailingMockRequest(SERVICE_INFO1, PREDICATE_URL, buildTimeSeriesAnalysisRequest(), exceptionToThrow);
    mockEngine.registerRequests(predictMockedRequest);

    try {
      timeSeriesAnalysisApi.predict(requestModel);
      fail("should fail since engine threw exception on predict");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceException(exception, "Failed on post predict to engine: Exception NPE raised while predicting: NullPointer");
    }
  }

  // --------------------------------------------
  //                  ACCURACY
  //---------------------------------------------

  @Test
  public void should_return_accuracy_result_from_engine_on_compute_forecast_accuracy() {
    TimeSeriesAnalysisRequestModel requestModel = buildTimeSeriesAnalysisRequestModel();
    MockedRequest accuracyMockedRequest = buildMockRequest(SERVICE_INFO1, FORECAST_ACCURACY_URL, buildTimeSeriesAnalysisRequest(), 2.0);
    mockEngine.registerRequests(accuracyMockedRequest);

    ResponseEntity<BigDecimal> actualResponse = timeSeriesAnalysisApi.computeForecastAccuracy(requestModel);

    assertOnResponseEntity(actualResponse, valueOf(2.0));
  }

  @Test
  public void should_throw_exception_if_engine_returns_an_exception_on_compute_accuracy() {
    TimeSeriesAnalysisRequestModel requestModel = buildTimeSeriesAnalysisRequestModel();
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception NPE raised while computing accuracy: NullPointer");
    MockedRequest accuracyMockedRequest = buildFailingMockRequest(SERVICE_INFO1, FORECAST_ACCURACY_URL, buildTimeSeriesAnalysisRequest(), exceptionToThrow);
    mockEngine.registerRequests(accuracyMockedRequest);

    try {
      timeSeriesAnalysisApi.computeForecastAccuracy(requestModel);
      fail("should fail since engine threw exception on compute accuracy");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceException(exception, "Failed on post computeForecastAccuracy to engine: Exception NPE raised while computing accuracy: NullPointer");
    }
  }

  // --------------------------------------------
  //                  FORECAST VS ACTUAL
  //---------------------------------------------

  @Test
  public void should_return_time_series_from_engine_on_forecast_vs_actual() {
    TimeSeriesAnalysisRequestModel requestModel = buildTimeSeriesAnalysisRequestModel();
    MockedRequest forecastVsActualMockedRequest = buildMockRequest(SERVICE_INFO1, FORECAST_URL, buildTimeSeriesAnalysisExpectedRequestForecastVsActual(), buildTimeSeriesResult());
    mockEngine.registerRequests(forecastVsActualMockedRequest);

    ResponseEntity<TimeSeriesModel> actualResponse = timeSeriesAnalysisApi.forecastVsActual(requestModel);

    assertOnResponseEntity(actualResponse, buildTimeSeriesModelResult());
  }

  @Test
  public void should_throw_exception_if_engine_returns_an_exception_on_forecast_vs_actual() {
    TimeSeriesAnalysisRequestModel requestModel = buildTimeSeriesAnalysisRequestModel();
    HttpServerErrorException exceptionToThrow = buildHttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception NPE raised while computing forecast: NullPointer");
    MockedRequest forecastVsActualMockedRequest = buildFailingMockRequest(SERVICE_INFO1, FORECAST_URL, buildTimeSeriesAnalysisExpectedRequestForecastVsActual(), exceptionToThrow);
    mockEngine.registerRequests(forecastVsActualMockedRequest);

    try {
      timeSeriesAnalysisApi.forecastVsActual(requestModel);
      fail("should fail since engine threw exception on compute forecast vs actual");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceException(exception, "Failed on post forecast to engine: Exception NPE raised while computing forecast: NullPointer");
    }
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
    MockedRequest predicateMockedRequest = buildMockRequest(SERVICE_INFO2, PREDICATE_URL, buildTimeSeriesAnalysisRequest(), buildTimeSeriesResult2());
    mockEngine.registerRequests(forecastMockedRequest, predicateMockedRequest);

    CompletableFuture<ResponseEntity<TimeSeriesModel>> actualForecastFuture = supplyAsync(() -> timeSeriesAnalysisApi.forecast(forecastRequestModel));
    mockEngine.waitUntilEngineCall();
    ResponseEntity<TimeSeriesModel> actualPredict = timeSeriesAnalysisApi.predict(predictRequestModel);
    forecastMockedRequest.countDownLatch.countDown();
    ResponseEntity<TimeSeriesModel> actualForecast = actualForecastFuture.join();

    assertOnResponseEntity(actualForecast, buildTimeSeriesModelResult());
    assertOnResponseEntity(actualPredict, buildTimeSeriesModelResult2());
    assertEquals(WAITING, engines.get(0).getState());
    assertEquals(WAITING, engines.get(1).getState());
  }

  @Test
  public void should_handle_requests_in_parallel_on_engines() throws Exception {
    mockEngine.setupWaitUntilEngineCall();
    TimeSeriesAnalysisRequestModel forecastRequestModel = buildTimeSeriesAnalysisRequestModel();
    TimeSeriesAnalysisRequestModel predictRequestModel = buildTimeSeriesAnalysisRequestModel();
    MockedRequest forecastMockedRequest = buildHangingMockRequest(SERVICE_INFO1, FORECAST_URL, buildTimeSeriesAnalysisRequest(), buildTimeSeriesResult());
    MockedRequest predicateMockedRequest = buildMockRequest(SERVICE_INFO2, PREDICATE_URL, buildTimeSeriesAnalysisRequest(), buildTimeSeriesResult2());
    mockEngine.registerRequests(forecastMockedRequest, predicateMockedRequest);

    CompletableFuture<ResponseEntity<TimeSeriesModel>> actualForecastFuture = supplyAsync(() -> timeSeriesAnalysisApi.forecast(forecastRequestModel));
    mockEngine.waitUntilEngineCall();
    timeSeriesAnalysisApi.predict(predictRequestModel);
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
    TimeSeriesAnalysisRequestModel forecastRequestModel = buildTimeSeriesAnalysisRequestModel();
    TimeSeriesAnalysisRequestModel predictRequestModel = buildTimeSeriesAnalysisRequestModel();
    MockedRequest forecastMockedRequest = buildHangingMockRequest(SERVICE_INFO1, FORECAST_URL, buildTimeSeriesAnalysisRequest(), buildTimeSeriesResult());
    MockedRequest forecastAccuracyMockedRequest = buildHangingMockRequest(SERVICE_INFO2, FORECAST_ACCURACY_URL, buildTimeSeriesAnalysisRequest(), 2.);
    mockEngine.registerRequests(forecastMockedRequest, forecastAccuracyMockedRequest);

    try {
      ExecutorService executor = Executors.newFixedThreadPool(2);
      supplyAsync(() -> timeSeriesAnalysisApi.forecast(forecastRequestModel), executor);
      mockEngine.waitUntilEngineCall();
      supplyAsync(() -> timeSeriesAnalysisApi.computeForecastAccuracy(forecastRequestModel), executor);
      mockEngine.waitUntilEngineCall();
      timeSeriesAnalysisApi.predict(predictRequestModel);
      fail("should fail since three requests sent and two engines are busy");

    } catch (Exception exception) {
      assertOnTimeSeriesAnalysisServiceException(exception, "No available engine to run time-series-predict please try again later");
    }
  }

  private static TimeSeries buildTimeSeriesResult() {
    TimeSeriesRow row1 = new TimeSeriesRow("date1", 1.);
    TimeSeriesRow row2 = new TimeSeriesRow("date2", 2.);

    List<TimeSeriesRow> rows = newArrayList(row1, row2);
    return new TimeSeries(rows, "dateColumnName", "valueColumnName", "yyyy-MM");
  }

  private static TimeSeriesModel buildTimeSeriesModelResult() {
    TimeSeriesRowModel row1 = buildTimeSeriesRowModel("date1", valueOf(1.));
    TimeSeriesRowModel row2 = buildTimeSeriesRowModel("date2", valueOf(2.));

    List<TimeSeriesRowModel> rows = newArrayList(row1, row2);
    return buildTimeSeriesModel(rows, "dateColumnName", "valueColumnName", "yyyy-MM");
  }

  private static TimeSeries buildTimeSeriesResult2() {
    TimeSeriesRow row1 = new TimeSeriesRow("date3", 3.);
    TimeSeriesRow row2 = new TimeSeriesRow("date4", 4.);

    List<TimeSeriesRow> rows = newArrayList(row1, row2);
    return new TimeSeries(rows, "date", "value", "yyyy");
  }

  private static TimeSeriesModel buildTimeSeriesModelResult2() {
    TimeSeriesRowModel row1 = buildTimeSeriesRowModel("date3", valueOf(3.));
    TimeSeriesRowModel row2 = buildTimeSeriesRowModel("date4", valueOf(4.));

    List<TimeSeriesRowModel> rows = newArrayList(row1, row2);
    return buildTimeSeriesModel(rows, "date", "value", "yyyy");
  }

  private static TimeSeriesAnalysisRequest buildTimeSeriesAnalysisRequest() {
    TimeSeriesRow row1 = new TimeSeriesRow("date-1", 11.);
    TimeSeriesRow row2 = new TimeSeriesRow("date0", 22.);
    TimeSeriesRow row3 = new TimeSeriesRow("date1", 33.);
    TimeSeriesRow row4 = new TimeSeriesRow("date2", 44.);

    List<TimeSeriesRow> rows = newArrayList(row1, row2, row3, row4);
    TimeSeries timeSeries = new TimeSeries(rows, "dateColumnName", "valueColumnName", "yyyy-MM");

    return new TimeSeriesAnalysisRequest(timeSeries, 2);
  }

  private static TimeSeriesAnalysisRequestModel buildTimeSeriesAnalysisRequestModel() {
    TimeSeriesRowModel row1 = buildTimeSeriesRowModel("date-1", valueOf(11.));
    TimeSeriesRowModel row2 = buildTimeSeriesRowModel("date0", valueOf(22.));
    TimeSeriesRowModel row3 = buildTimeSeriesRowModel("date1", valueOf(33.));
    TimeSeriesRowModel row4 = buildTimeSeriesRowModel("date2", valueOf(44.));

    List<TimeSeriesRowModel> rows = newArrayList(row1, row2, row3, row4);
    TimeSeriesModel timeSeries = buildTimeSeriesModel(rows, "dateColumnName", "valueColumnName", "yyyy-MM");

    return TimeSeriesModelHelper.buildTimeSeriesAnalysisRequestModel(timeSeries, 2);
  }

  private static TimeSeriesAnalysisRequest buildTimeSeriesAnalysisExpectedRequestForecastVsActual() {
    TimeSeriesRow row1 = new TimeSeriesRow("date-1", 11.);
    TimeSeriesRow row2 = new TimeSeriesRow("date0", 22.);

    List<TimeSeriesRow> rows = newArrayList(row1, row2);
    TimeSeries timeSeries = new TimeSeries(rows, "dateColumnName", "valueColumnName", "yyyy-MM");

    return new TimeSeriesAnalysisRequest(timeSeries, 2);
  }

  private static void assertOnTimeSeriesAnalysisServiceException(Exception exception, String exceptionMessage) {
    assertInstanceOf(TimeSeriesAnalysisServiceException.class, exception);
    assertEquals(exceptionMessage, exception.getMessage());
  }

  private static <T> void assertOnResponseEntity(ResponseEntity<T> actualResponse, T expectedBody) {
    assertEquals(OK, actualResponse.getStatusCode());
    assertEquals(expectedBody, actualResponse.getBody());
  }
}
