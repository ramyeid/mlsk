package org.mlsk.service.impl.orchestrator.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.lib.model.Endpoint;
import org.mlsk.service.engine.Engine;
import org.mlsk.service.impl.orchestrator.exception.NoAvailableEngineException;
import org.mlsk.service.impl.orchestrator.exception.NoBlockedEngineException;
import org.mlsk.service.impl.orchestrator.exception.NoEngineWithInformationException;
import org.mlsk.service.impl.orchestrator.request.model.Request;
import org.mlsk.service.impl.orchestrator.request.registry.RequestRegistry;
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

  private static final Endpoint ENDPOINT1 = new Endpoint("host1", 123L);
  private static final Endpoint ENDPOINT2 = new Endpoint("host2", 1234L);
  private static final Endpoint UNAVAILABLE_ENDPOINT = new Endpoint("unavailable", 12345L);
  private static final String ACTION = "ACTION";

  @Mock
  private Engine engine1;
  @Mock
  private Engine engine2;
  @Mock
  private RequestRegistry requestRegistry;

  private OrchestratorImpl orchestrator;

  @BeforeEach
  public void setUp() {
    this.orchestrator = new OrchestratorImpl(newArrayList(engine1, engine2), requestRegistry);
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
  public void should_throw_exception_if_no_engine_is_available_for_action_on_book_engine_run_and_complete() {
    onGetStateReturn(engine1, COMPUTING);
    onGetStateReturn(engine2, COMPUTING);

    try {
      orchestrator.bookEngineRunAndComplete(1L, ACTION, buildFunction());
      fail("should fail because no engine is available");

    } catch (Exception exception) {
      assertOnNoAvailableEngineException(exception);
    }
  }

  @Test
  public void should_rethrow_exception_if_action_fails_on_book_engine_run_and_complete() {
    long requestId = 1L;
    onGetRequest(requestId, ENDPOINT2);
    onGetEndpointReturn(engine1, ENDPOINT1);
    onGetEndpointReturn(engine2, ENDPOINT2);
    onGetStateReturn(engine1, COMPUTING);
    onGetStateReturn(engine2, WAITING);
    throwExceptionOnAction(engine2, new IllegalArgumentException("runtime exception"));

    try {
      orchestrator.bookEngineRunAndComplete(requestId, ACTION, buildFunction());
      fail("should fail since engine threw an exception");

    } catch (Exception exception) {
      assertInstanceOf(IllegalArgumentException.class, exception);
      assertEquals("runtime exception", exception.getMessage());
    }
  }

  @Test
  public void should_release_engine_if_action_fails_on_book_engine_run_and_complete() {
    long requestId = 2L;
    onGetRequest(requestId, ENDPOINT2);
    onGetEndpointReturn(engine1, ENDPOINT1);
    onGetEndpointReturn(engine2, ENDPOINT2);
    onGetStateReturn(engine1, COMPUTING);
    onGetStateReturn(engine2, WAITING);
    throwExceptionOnAction(engine2, new IllegalArgumentException("runtime exception"));

    try {
      orchestrator.bookEngineRunAndComplete(requestId, ACTION, buildFunction());
      fail("should fail since engine threw an exception");

    } catch (Exception ignored) {
    }
    InOrder inOrder = buildInOrder();
    inOrder.verify(engine2).bookEngine();
    inOrder.verify(requestRegistry).register(requestId, ENDPOINT2);
    inOrder.verify(engine2).markAsComputing();
    inOrder.verify(engine2).predict(any(TimeSeriesAnalysisRequest.class));
    inOrder.verify(requestRegistry).get(requestId);
    inOrder.verify(engine2).markAsWaitingForRequest();
    inOrder.verify(requestRegistry).remove(requestId);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_push_action_on_available_engine_on_book_engine_run_and_complete() {
    long requestId = 3L;
    onGetRequest(requestId, ENDPOINT2);
    onGetEndpointReturn(engine1, ENDPOINT1);
    onGetEndpointReturn(engine2, ENDPOINT2);
    onGetStateReturn(engine1, COMPUTING);
    onGetStateReturn(engine2, WAITING);

    orchestrator.bookEngineRunAndComplete(requestId, ACTION, buildFunction());

    InOrder inOrder = buildInOrder();
    inOrder.verify(engine2).bookEngine();
    inOrder.verify(requestRegistry).register(requestId, ENDPOINT2);
    inOrder.verify(engine2).markAsComputing();
    inOrder.verify(engine2).predict(any(TimeSeriesAnalysisRequest.class));
    inOrder.verify(requestRegistry).get(requestId);
    inOrder.verify(engine2).markAsWaitingForRequest();
    inOrder.verify(requestRegistry).remove(requestId);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_result_on_book_engine_run_and_complete() {
    long requestId = 4L;
    onGetRequest(requestId, ENDPOINT2);
    onGetEndpointReturn(engine1, ENDPOINT1);
    onGetEndpointReturn(engine2, ENDPOINT2);
    onGetStateReturn(engine1, COMPUTING);
    onGetStateReturn(engine2, WAITING);
    doReturnOnAction(engine2, buildTimeSeries());

    TimeSeries result = orchestrator.bookEngineRunAndComplete(requestId, ACTION, buildFunction());

    assertEquals(buildTimeSeries(), result);
  }

  @Test
  public void should_throw_exception_if_no_available_engine_when_booking_engine() {
    long requestId = 4L;
    onGetStateReturn(engine1, COMPUTING);
    onGetStateReturn(engine2, COMPUTING);

    try {
      orchestrator.bookEngine(requestId, ACTION);
      fail("should fail because no engine is available");

    } catch (Exception exception) {
      assertOnNoAvailableEngineException(exception);
    }
  }

  @Test
  public void should_book_engine_and_register_new_request() {
    long requestId = 5L;
    onGetEndpointReturn(engine1, ENDPOINT1);
    onGetStateReturn(engine1, WAITING);

    orchestrator.bookEngine(requestId, ACTION);

    InOrder inOrder = buildInOrder();
    inOrder.verify(engine1).getState();
    inOrder.verify(engine1).bookEngine();
    inOrder.verify(engine1).getEndpoint();
    inOrder.verify(requestRegistry).register(requestId, ENDPOINT1);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_newly_booked_engine_on_book_engine() {
    long requestId = 6L;
    onGetEndpointReturn(engine1, ENDPOINT1);
    onGetStateReturn(engine1, WAITING);

    Engine bookedEngine = orchestrator.bookEngine(requestId, ACTION);

    assertEquals(ENDPOINT1, bookedEngine.getEndpoint());
  }

  @Test
  public void should_throw_exception_if_request_not_found_when_running_on_engine() {
    long requestId = 7L;
    onGetRequest(requestId, null);

    try {
      orchestrator.runOnEngine(requestId, ACTION, buildFunction());
      fail("should fail since no booked engine");

    } catch (Exception exception) {
      assertOnNoAvailableBlockedEngineException(exception, requestId);
    }
  }

  @Test
  public void should_rethrow_exception_if_action_fails_on_run_on_engine_with_request_id() {
    long requestId = 7L;
    onGetRequest(requestId, ENDPOINT2);
    onGetEndpointReturn(engine1, ENDPOINT1);
    onGetEndpointReturn(engine2, ENDPOINT2);
    throwExceptionOnAction(engine2, new IllegalArgumentException("runtime exception"));

    try {
      orchestrator.runOnEngine(requestId, ACTION, buildFunction());
      fail("should fail since engine threw an exception");

    } catch (Exception exception) {
      assertInstanceOf(IllegalArgumentException.class, exception);
      assertEquals("runtime exception", exception.getMessage());
    }
  }

  @Test
  public void should_throw_exception_if_request_found_but_no_engine_with_info_when_running_on_engine() {
    long requestId = 7L;
    onGetRequest(requestId, UNAVAILABLE_ENDPOINT);
    onGetEndpointReturn(engine1, ENDPOINT1);
    onGetEndpointReturn(engine2, ENDPOINT2);

    try {
      orchestrator.runOnEngine(requestId, ACTION, buildFunction());
      fail("should fail since no engine with service info found");

    } catch (Exception exception) {
      assertOnNoEngineWithInformationException(exception);
    }
  }

  @Test
  public void should_retrieve_request_and_run_on_engine_with_request_id() {
    long requestId = 8L;
    onGetRequest(requestId, ENDPOINT2);
    onGetEndpointReturn(engine1, ENDPOINT1);
    onGetEndpointReturn(engine2, ENDPOINT2);

    orchestrator.runOnEngine(requestId, ACTION, buildFunction());

    InOrder inOrder = buildInOrder();
    inOrder.verify(requestRegistry).get(requestId);
    inOrder.verify(engine2).markAsComputing();
    inOrder.verify(engine2).predict(any(TimeSeriesAnalysisRequest.class));
    inOrder.verify(engine2).bookEngine();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_result_on_run_on_engine_with_request_id() {
    long requestId = 10L;
    onGetRequest(requestId, ENDPOINT2);
    onGetEndpointReturn(engine1, ENDPOINT1);
    onGetEndpointReturn(engine2, ENDPOINT2);
    doReturnOnAction(engine2, buildTimeSeries());

    TimeSeries result = orchestrator.runOnEngine(requestId, ACTION, buildFunction());

    assertEquals(buildTimeSeries(), result);
  }

  @Test
  public void should_return_completed_request() {
    long requestId = 11L;
    onGetRequest(requestId, ENDPOINT1);
    onGetEndpointReturn(engine1, ENDPOINT1);

    Optional<Request> actualCompletedRequest = orchestrator.completeRequest(requestId, ACTION);

    Request expectedRequest = new Request(requestId, ENDPOINT1);
    assertTrue(actualCompletedRequest.isPresent());
    assertEquals(expectedRequest, actualCompletedRequest.get());
  }

  @Test
  public void should_return_empty_optional_if_no_request_to_complete() {
    long requestId = 11L;
    onGetRequest(requestId, null);

    Optional<Request> actualCompletedRequest = orchestrator.completeRequest(requestId, ACTION);

    assertTrue(actualCompletedRequest.isEmpty());
  }

  @Test
  public void should_remove_request_and_mark_as_waiting_on_complete_request() {
    long requestId = 11L;
    onGetRequest(requestId, ENDPOINT1);
    onGetEndpointReturn(engine1, ENDPOINT1);

    orchestrator.completeRequest(requestId, ACTION);

    InOrder inOrder = buildInOrder();
    inOrder.verify(requestRegistry).get(requestId);
    inOrder.verify(engine1).markAsWaitingForRequest();
    inOrder.verify(requestRegistry).remove(requestId);
    inOrder.verifyNoMoreInteractions();
  }

  private InOrder buildInOrder() {
    return inOrder(engine1, engine2, requestRegistry);
  }

  private static void onGetEndpointReturn(Engine engine, Endpoint endpoint) {
    when(engine.getEndpoint()).thenReturn(endpoint);
  }

  private void onGetRequest(long requestId, Endpoint endpoint) {
    Optional<Request> request = ofNullable(endpoint).map(info -> new Request(requestId, info));
    when(requestRegistry.get(requestId)).thenReturn(request);
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

  private static void assertOnNoAvailableBlockedEngineException(Exception exception, long requestId) {
    assertInstanceOf(NoBlockedEngineException.class, exception);
    assertEquals(format("No available engine with %s to run %s", requestId, ACTION), exception.getMessage());
  }

  private static void assertOnNoEngineWithInformationException(Exception exception) {
    assertInstanceOf(NoEngineWithInformationException.class, exception);
    assertEquals(format("No engine found with endpoint Endpoint{host='unavailable', port='12345'} to run %s - NOT EXPECTED - check logs!", ACTION), exception.getMessage());
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