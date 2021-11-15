package org.mlsk.service.impl.orchestrator.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.service.engine.Engine;
import org.mlsk.service.impl.orchestrator.exception.NoAvailableEngineException;
import org.mlsk.service.impl.orchestrator.request.RequestHandler;
import org.mlsk.service.impl.orchestrator.request.model.Request;
import org.mlsk.service.model.engine.EngineState;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.model.timeseries.TimeSeriesRow;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mlsk.service.model.engine.EngineState.COMPUTING;
import static org.mlsk.service.model.engine.EngineState.WAITING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrchestratorImplTest {

  private static final ServiceInformation SERVICE_INFO1 = new ServiceInformation("host1", 123L);
  private static final ServiceInformation SERVICE_INFO2 = new ServiceInformation("host2", 1234L);
  private static final ServiceInformation UNAVAILABLE_SERVICE_INFO = new ServiceInformation("unavailable", 12345L);
  private static final String ACTION = "ACTION";

  @Mock
  private Engine engine1;
  @Mock
  private Engine engine2;
  @Mock
  private RequestHandler requestHandler;

  private OrchestratorImpl orchestrator;

  @BeforeEach
  public void setUp() {
    this.orchestrator = new OrchestratorImpl(newArrayList(engine1, engine2), requestHandler);
  }

  @Test
  public void should_launch_all_engines() {

    orchestrator.launchEngines();

    InOrder inOrder = buildInOrder();
    inOrder.verify(engine1).launchEngine();
    inOrder.verify(engine2).launchEngine();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_exception_if_no_engine_is_available_for_action_on_run_on_engine() {
    onGetStateReturn(engine1, COMPUTING);
    onGetStateReturn(engine2, COMPUTING);

    try {
      orchestrator.runOnEngine(buildFunction(), ACTION);
      fail("should fail because no engine is available");

    } catch (Exception exception) {
      assertOnNoAvailableEngineException(exception);
    }
  }

  @Test
  public void should_rethrow_exception_if_action_fails_on_run_on_engine() {
    String requestId = "requestId";
    onRegisterNewRequestReturn(SERVICE_INFO2, requestId);
    onGetRequest(requestId, SERVICE_INFO2);
    onGetServiceInformationReturn(engine1, SERVICE_INFO1);
    onGetServiceInformationReturn(engine2, SERVICE_INFO2);
    onGetStateReturn(engine1, COMPUTING);
    onGetStateReturn(engine2, WAITING);
    throwExceptionOnAction(engine2, new IllegalArgumentException("runtime exception"));

    try {
      orchestrator.runOnEngine(buildFunction(), ACTION);
      fail("should fail since engine threw an exception");

    } catch (Exception exception) {
      assertInstanceOf(IllegalArgumentException.class, exception);
      assertEquals("runtime exception", exception.getMessage());
    }
  }

  @Test
  public void should_release_engine__if_action_fails_on_run_on_engine() {
    String requestId = "requestId";
    onRegisterNewRequestReturn(SERVICE_INFO2, requestId);
    onGetRequest(requestId, SERVICE_INFO2);
    onGetServiceInformationReturn(engine1, SERVICE_INFO1);
    onGetServiceInformationReturn(engine2, SERVICE_INFO2);
    onGetStateReturn(engine1, COMPUTING);
    onGetStateReturn(engine2, WAITING);
    throwExceptionOnAction(engine2, new IllegalArgumentException("runtime exception"));

    try {
      orchestrator.runOnEngine(buildFunction(), ACTION);
      fail("should fail since engine threw an exception");

    } catch (Exception ignored) {
    }
    InOrder inOrder = buildInOrder();
    inOrder.verify(engine2).bookEngine();
    inOrder.verify(requestHandler).registerNewRequest(ACTION, SERVICE_INFO2);
    inOrder.verify(engine2).markAsComputing();
    inOrder.verify(engine2).predict(any(TimeSeriesAnalysisRequest.class));
    inOrder.verify(requestHandler, times(2)).getRequest(requestId);
    inOrder.verify(engine2).markAsWaitingForRequest();
    inOrder.verify(requestHandler).removeRequest(requestId);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_push_action_on_available_engine_on_run_on_engine() {
    String requestId = "requestId";
    onRegisterNewRequestReturn(SERVICE_INFO2, requestId);
    onGetRequest(requestId, SERVICE_INFO2);
    onGetServiceInformationReturn(engine1, SERVICE_INFO1);
    onGetServiceInformationReturn(engine2, SERVICE_INFO2);
    onGetStateReturn(engine1, COMPUTING);
    onGetStateReturn(engine2, WAITING);

    orchestrator.runOnEngine(buildFunction(), ACTION);

    InOrder inOrder = buildInOrder();
    inOrder.verify(engine2).bookEngine();
    inOrder.verify(requestHandler).registerNewRequest(ACTION, SERVICE_INFO2);
    inOrder.verify(engine2).markAsComputing();
    inOrder.verify(engine2).predict(any(TimeSeriesAnalysisRequest.class));
    inOrder.verify(requestHandler, times(2)).getRequest(requestId);
    inOrder.verify(engine2).markAsWaitingForRequest();
    inOrder.verify(requestHandler).removeRequest(requestId);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_result_on_run_on_engine() {
    String requestId = "requestId";
    onRegisterNewRequestReturn(SERVICE_INFO2, requestId);
    onGetRequest(requestId, SERVICE_INFO2);
    onGetServiceInformationReturn(engine1, SERVICE_INFO1);
    onGetServiceInformationReturn(engine2, SERVICE_INFO2);
    onGetStateReturn(engine1, COMPUTING);
    onGetStateReturn(engine2, WAITING);
    doReturnOnAction(engine2, buildTimeSeries());

    TimeSeries result = orchestrator.runOnEngine(buildFunction(), ACTION);

    assertEquals(buildTimeSeries(), result);
  }

  @Test
  public void should_throw_exception_if_no_available_engine_when_booking_engine() {
    onGetStateReturn(engine1, COMPUTING);
    onGetStateReturn(engine2, COMPUTING);

    try {
      orchestrator.bookEngine(ACTION);
      fail("should fail because no engine is available");

    } catch (Exception exception) {
      assertOnNoAvailableEngineException(exception);
    }
  }

  @Test
  public void should_book_engine_and_register_new_request() {
    String requestId = "requestId";
    onRegisterNewRequestReturn(SERVICE_INFO1, requestId);
    onGetServiceInformationReturn(engine1, SERVICE_INFO1);
    onGetStateReturn(engine1, WAITING);

    orchestrator.bookEngine(ACTION);

    InOrder inOrder = buildInOrder();
    inOrder.verify(engine1).getState();
    inOrder.verify(engine1).bookEngine();
    inOrder.verify(engine1).getServiceInformation();
    inOrder.verify(requestHandler).registerNewRequest(ACTION, SERVICE_INFO1);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_newly_register_request_on_book_engine() {
    String requestId = "requestId";
    onRegisterNewRequestReturn(SERVICE_INFO1, requestId);
    onGetServiceInformationReturn(engine1, SERVICE_INFO1);
    onGetStateReturn(engine1, WAITING);

    String actualRequest = orchestrator.bookEngine(ACTION);

    assertEquals(requestId, actualRequest);
  }

  @Test
  public void should_throw_exception_if_request_not_found_when_running_on_engine() {
    String requestId = "requestId";
    onGetRequest(requestId, null);

    try {
      orchestrator.runOnEngine(requestId, buildFunction(), ACTION);
      fail("should fail since no booked engine");

    } catch (Exception exception) {
      assertOnNoAvailableBlockedEngineException(exception, requestId);
    }
  }

  @Test
  public void should_rethrow_exception_if_action_fails_on_run_on_engine_with_request_id() {
    String requestId = "requestId";
    onGetRequest(requestId, SERVICE_INFO2);
    onGetServiceInformationReturn(engine1, SERVICE_INFO1);
    onGetServiceInformationReturn(engine2, SERVICE_INFO2);
    throwExceptionOnAction(engine2, new IllegalArgumentException("runtime exception"));

    try {
      orchestrator.runOnEngine(requestId, buildFunction(), ACTION);
      fail("should fail since engine threw an exception");

    } catch (Exception exception) {
      assertInstanceOf(IllegalArgumentException.class, exception);
      assertEquals("runtime exception", exception.getMessage());
    }
  }

  @Test
  public void should_throw_exception_if_request_found_but_no_engine_with_info_when_running_on_engine() {
    String requestId = "requestId";
    onGetRequest(requestId, UNAVAILABLE_SERVICE_INFO);
    onGetServiceInformationReturn(engine1, SERVICE_INFO1);
    onGetServiceInformationReturn(engine2, SERVICE_INFO2);

    try {
      orchestrator.runOnEngine(requestId, buildFunction(), ACTION);
      fail("should fail since no engine with service info found");

    } catch (Exception exception) {
      assertOnNoEngineWithInformationException(exception);
    }
  }

  @Test
  public void should_retrieve_request_and_run_on_engine_with_request_id() {
    String requestId = "requestId";
    onGetRequest(requestId, SERVICE_INFO2);
    onGetServiceInformationReturn(engine1, SERVICE_INFO1);
    onGetServiceInformationReturn(engine2, SERVICE_INFO2);

    orchestrator.runOnEngine(requestId, buildFunction(), ACTION);

    InOrder inOrder = buildInOrder();
    inOrder.verify(requestHandler).getRequest(requestId);
    inOrder.verify(engine2).markAsComputing();
    inOrder.verify(engine2).predict(any(TimeSeriesAnalysisRequest.class));
    inOrder.verify(engine2).bookEngine();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_result_on_run_on_engine_with_request_id() {
    String requestId = "requestId";
    onGetRequest(requestId, SERVICE_INFO2);
    onGetServiceInformationReturn(engine1, SERVICE_INFO1);
    onGetServiceInformationReturn(engine2, SERVICE_INFO2);
    doReturnOnAction(engine2, buildTimeSeries());

    TimeSeries result = orchestrator.runOnEngine(requestId, buildFunction(), ACTION);

    assertEquals(buildTimeSeries(), result);
  }

  @Test
  public void should_throw_exception_if_no_request_on_release() {
    String requestId = "requestId";
    onGetRequest(requestId, null);

    try {
      orchestrator.releaseEngine(requestId, ACTION);
      fail("should fail since no request found");

    } catch (Exception exception) {
      assertOnNoAvailableBlockedEngineException(exception, requestId);
    }
  }

  @Test
  public void should_remove_request_and_mark_as_waiting_on_release() {
    String requestId = "requestId";
    onGetRequest(requestId, SERVICE_INFO1);
    onGetServiceInformationReturn(engine1, SERVICE_INFO1);

    orchestrator.releaseEngine(requestId, ACTION);

    InOrder inOrder = buildInOrder();
    inOrder.verify(requestHandler, times(2)).getRequest(requestId);
    inOrder.verify(engine1).markAsWaitingForRequest();
    inOrder.verify(requestHandler).removeRequest(requestId);
    inOrder.verifyNoMoreInteractions();
  }

  private InOrder buildInOrder() {
    return inOrder(engine1, engine2, requestHandler);
  }

  private static void onGetServiceInformationReturn(Engine engine, ServiceInformation serviceInformation) {
    when(engine.getServiceInformation()).thenReturn(serviceInformation);
  }

  private void onRegisterNewRequestReturn(ServiceInformation serviceInformation, String requestId) {
    when(requestHandler.registerNewRequest(ACTION, serviceInformation)).thenReturn(requestId);
  }

  private void onGetRequest(String requestId, ServiceInformation serviceInformation) {
    Optional<Request> request = ofNullable(serviceInformation).map(info -> new Request(ACTION, info));
    when(requestHandler.getRequest(requestId)).thenReturn(request);
  }

  private void onGetStateReturn(Engine engine, EngineState computing) {
    when(engine.getState()).thenReturn(computing);
  }

  private void doReturnOnAction(Engine engine, TimeSeries timeSeries) {
    when(engine.predict(any(TimeSeriesAnalysisRequest.class))).thenReturn(timeSeries);
  }

  private static void throwExceptionOnAction(Engine engine, Exception exception) {
    doThrow(exception).when(engine).predict(any(TimeSeriesAnalysisRequest.class));
  }

  private static void assertOnNoAvailableEngineException(Exception exception) {
    assertInstanceOf(NoAvailableEngineException.class, exception);
    assertEquals(format("No available engine to run %s, please try again later", ACTION), exception.getMessage());
  }

  private static void assertOnNoAvailableBlockedEngineException(Exception exception, String requestId) {
    assertInstanceOf(NoAvailableEngineException.class, exception);
    assertEquals(format("No available engine with %s to run %s", requestId, ACTION), exception.getMessage());
  }

  private static void assertOnNoEngineWithInformationException(Exception exception) {
    assertInstanceOf(NoAvailableEngineException.class, exception);
    assertEquals(format("No engine found with information ServiceInformation{host='unavailable', port='12345'} to run %s - NOT EXPECTED - check logs!", ACTION), exception.getMessage());
  }

  private static TimeSeries buildTimeSeries() {
    TimeSeriesRow row1 = new TimeSeriesRow("date1", 123.);
    TimeSeriesRow row2 = new TimeSeriesRow("date2", 124.);
    return new TimeSeries(newArrayList(row1, row2), "date", "value", "yyyyMM");
  }

  private static Function<Engine, TimeSeries> buildFunction() {
    return engine -> engine.predict(mock(TimeSeriesAnalysisRequest.class));
  }
}