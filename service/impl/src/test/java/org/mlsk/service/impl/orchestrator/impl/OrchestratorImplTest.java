package org.mlsk.service.impl.orchestrator.impl;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.lib.model.Endpoint;
import org.mlsk.service.admin.AdminEngine;
import org.mlsk.service.engine.Engine;
import org.mlsk.service.impl.engine.impl.EngineImpl;
import org.mlsk.service.impl.orchestrator.exception.NoAvailableEngineException;
import org.mlsk.service.impl.orchestrator.exception.NoBlockedEngineException;
import org.mlsk.service.impl.orchestrator.exception.NoEngineWithInformationException;
import org.mlsk.service.impl.orchestrator.request.model.Request;
import org.mlsk.service.impl.orchestrator.request.registry.RequestRegistry;
import org.mlsk.service.model.admin.EngineDetailResponse;
import org.mlsk.service.model.admin.ProcessDetailResponse;
import org.mlsk.service.model.engine.EngineState;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.model.timeseries.TimeSeriesRow;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mlsk.service.impl.setup.ServiceConfiguration.buildServiceConfiguration;
import static org.mlsk.service.model.engine.EngineState.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrchestratorImplTest {

  private static final Endpoint ENDPOINT1 = new Endpoint("host1", 123L);
  private static final Endpoint ENDPOINT2 = new Endpoint("host2", 1234L);
  private static final Endpoint UNAVAILABLE_ENDPOINT = new Endpoint("unavailable", 12345L);
  private static final String ACTION = "ACTION";

  @Mock
  private RequestRegistry requestRegistry;

  private OrchestratorImpl orchestrator;
  private Engine engine1Spy;
  private Engine engine2Spy;

  @BeforeEach
  public void setUp() throws ParseException {
    buildServiceConfiguration("", "--engine-ports", "4647,4648", "--logs-path", "logsPath", "-engine-path", "enginePath", "--log-level", "INFO", "--engine-log-level", "INFO");
    this.engine1Spy = spy(new EngineImpl(ENDPOINT1));
    this.engine2Spy = spy(new EngineImpl(ENDPOINT2));
    this.orchestrator = new OrchestratorImpl(newArrayList(engine1Spy, engine2Spy), requestRegistry);
  }

  @Test
  public void should_launch_all_engines() {
    doNothingOnLaunchEngine(engine1Spy);
    doNothingOnLaunchEngine(engine2Spy);

    orchestrator.launchEngines();

    InOrder inOrder = buildInOrder();
    inOrder.verify(engine1Spy).launchEngine(any());
    inOrder.verify(engine1Spy).markAsReadyForNewRequest();
    inOrder.verify(engine2Spy).launchEngine(any());
    inOrder.verify(engine2Spy).markAsReadyForNewRequest();
    inOrder.verifyNoMoreInteractions();
    verifyStates(IDLE, IDLE);
  }

  @Test
  public void should_set_status_as_off_if_launching_engine_fails() {
    doNothingOnLaunchEngine(engine1Spy);
    doThrowExceptionOnLaunchEngine(engine2Spy);

    try {
      orchestrator.launchEngines();
      fail("should fail since engines did not launch successfully");

    } catch (Exception exception) {
      assertInstanceOf(RuntimeException.class, exception);
      assertEquals("Exception while relaunch", exception.getMessage());
    }
    InOrder inOrder = buildInOrder();
    inOrder.verify(engine1Spy).launchEngine(any());
    inOrder.verify(engine1Spy).markAsReadyForNewRequest();
    inOrder.verify(engine2Spy).launchEngine(any());
    inOrder.verify(engine2Spy).markAsNotAvailable();
    inOrder.verifyNoMoreInteractions();
    verifyStates(IDLE, OFF);
  }

  @Test
  public void should_keep_state_off_when_engine_fail_to_relaunch() {
    doThrowExceptionOnLaunchEngine(engine1Spy);
    engine1Spy.markAsNotAvailable();

    orchestrator.onEngineKilled(engine1Spy);

    InOrder inOrder = buildInOrder();
    inOrder.verify(engine1Spy).markAsNotAvailable();
    inOrder.verify(requestRegistry).releaseAll(eq(ENDPOINT1));
    inOrder.verify(engine1Spy).launchEngine(any());
    inOrder.verify(engine1Spy).markAsNotAvailable();
    inOrder.verifyNoMoreInteractions();
    verifyStates(OFF, OFF);
  }

  @Test
  public void should_modify_state_and_relaunch_engine_when_killed() {
    engine1Spy.markAsNotAvailable();
    doNothingOnLaunchEngine(engine1Spy);

    orchestrator.onEngineKilled(engine1Spy);

    InOrder inOrder = buildInOrder();
    inOrder.verify(engine1Spy).markAsNotAvailable();
    inOrder.verify(requestRegistry).releaseAll(eq(ENDPOINT1));
    inOrder.verify(engine1Spy).launchEngine(any());
    inOrder.verify(engine1Spy).markAsReadyForNewRequest();
    inOrder.verifyNoMoreInteractions();
    verifyStates(IDLE, OFF);
  }

  @Test
  public void should_throw_exception_if_no_engine_is_available_for_action_on_book_engine_run_and_complete() {
    engine1Spy.markAsStartingAction();
    engine2Spy.markAsStartingAction();

    try {
      orchestrator.bookEngineRunAndComplete(1L, ACTION, buildFunction());
      fail("should fail because no engine is available");

    } catch (Exception exception) {
      assertOnNoAvailableEngineException(exception);
    }
    verifyStates(COMPUTING, COMPUTING);
  }

  @Test
  public void should_rethrow_exception_if_action_fails_on_book_engine_run_and_complete() {
    long requestId = 1L;
    onGetRequest(requestId, ENDPOINT2);
    throwExceptionOnAction(engine2Spy, new IllegalArgumentException("runtime exception"));
    engine1Spy.markAsStartingAction();
    engine2Spy.markAsReadyForNewRequest();

    try {
      orchestrator.bookEngineRunAndComplete(requestId, ACTION, buildFunction());
      fail("should fail since engine threw an exception");

    } catch (Exception exception) {
      assertInstanceOf(IllegalArgumentException.class, exception);
      assertEquals("runtime exception", exception.getMessage());
    }
    verifyStates(COMPUTING, IDLE);
  }

  @Test
  public void should_release_engine_if_action_fails_on_book_engine_run_and_complete() {
    long requestId = 2L;
    onGetRequest(requestId, ENDPOINT2);
    throwExceptionOnAction(engine2Spy, new IllegalArgumentException("runtime exception"));
    engine1Spy.markAsStartingAction();
    engine2Spy.markAsReadyForNewRequest();

    try {
      orchestrator.bookEngineRunAndComplete(requestId, ACTION, buildFunction());
      fail("should fail since engine threw an exception");

    } catch (Exception ignored) {
    }
    InOrder inOrder = buildInOrder();
    inOrder.verify(engine2Spy).markAsBooked();
    inOrder.verify(requestRegistry).register(requestId, ENDPOINT2);
    inOrder.verify(engine2Spy).markAsStartingAction();
    inOrder.verify(engine2Spy).predict(any(TimeSeriesAnalysisRequest.class));
    inOrder.verify(engine2Spy).markAsActionEnded();
    inOrder.verify(requestRegistry).get(requestId);
    inOrder.verify(requestRegistry).release(requestId);
    inOrder.verifyNoMoreInteractions();
    verifyStates(COMPUTING, IDLE);
  }

  @Test
  public void should_push_action_on_available_engine_on_book_engine_run_and_complete() {
    long requestId = 3L;
    onGetRequest(requestId, ENDPOINT2);
    doReturnOnAction(engine2Spy, buildTimeSeries());
    engine1Spy.markAsStartingAction();
    engine2Spy.markAsReadyForNewRequest();

    orchestrator.bookEngineRunAndComplete(requestId, ACTION, buildFunction());

    InOrder inOrder = buildInOrder();
    inOrder.verify(engine2Spy).markAsBooked();
    inOrder.verify(requestRegistry).register(requestId, ENDPOINT2);
    inOrder.verify(engine2Spy).markAsStartingAction();
    inOrder.verify(engine2Spy).predict(any(TimeSeriesAnalysisRequest.class));
    inOrder.verify(engine2Spy).markAsActionEnded();
    inOrder.verify(requestRegistry).get(requestId);
    inOrder.verify(requestRegistry).release(requestId);
    inOrder.verifyNoMoreInteractions();
    verifyStates(COMPUTING, IDLE);
  }

  @Test
  public void should_return_result_on_book_engine_run_and_complete() {
    long requestId = 4L;
    onGetRequest(requestId, ENDPOINT2);
    doReturnOnAction(engine2Spy, buildTimeSeries());
    engine1Spy.markAsStartingAction();
    engine2Spy.markAsReadyForNewRequest();

    TimeSeries result = orchestrator.bookEngineRunAndComplete(requestId, ACTION, buildFunction());

    assertEquals(buildTimeSeries(), result);
    verifyStates(COMPUTING, IDLE);
  }

  @Test
  public void should_throw_exception_if_no_available_engine_when_booking_engine() {
    long requestId = 4L;
    engine1Spy.markAsStartingAction();
    engine2Spy.markAsStartingAction();

    try {
      orchestrator.bookEngine(requestId, ACTION);
      fail("should fail because no engine is available");

    } catch (Exception exception) {
      assertOnNoAvailableEngineException(exception);
    }
    verifyStates(COMPUTING, COMPUTING);
  }

  @Test
  public void should_book_engine_and_register_new_request() {
    long requestId = 5L;
    engine1Spy.markAsReadyForNewRequest();

    orchestrator.bookEngine(requestId, ACTION);

    InOrder inOrder = buildInOrder();
    inOrder.verify(engine1Spy).getState();
    inOrder.verify(engine1Spy).markAsBooked();
    inOrder.verify(engine1Spy).getEndpoint();
    inOrder.verify(requestRegistry).register(requestId, ENDPOINT1);
    inOrder.verifyNoMoreInteractions();
    verifyStates(BOOKED, OFF);
  }

  @Test
  public void should_return_newly_booked_engine_on_book_engine() {
    long requestId = 6L;
    engine1Spy.markAsReadyForNewRequest();

    Engine bookedEngine = orchestrator.bookEngine(requestId, ACTION);

    assertEquals(ENDPOINT1, bookedEngine.getEndpoint());
    verifyStates(BOOKED, OFF);
  }

  @Test
  public void should_throw_exception_if_request_not_found_when_running_on_engine() {
    long requestId = 7L;
    onGetRequest(requestId, null);
    engine1Spy.markAsReadyForNewRequest();
    engine2Spy.markAsReadyForNewRequest();

    try {
      orchestrator.runOnEngine(requestId, ACTION, buildFunction());
      fail("should fail since no booked engine");

    } catch (Exception exception) {
      assertOnNoAvailableBlockedEngineException(exception, requestId);
    }
    verifyStates(IDLE, IDLE);
  }

  @Test
  public void should_rethrow_exception_if_action_fails_on_run_on_engine_with_request_id() {
    long requestId = 7L;
    onGetRequest(requestId, ENDPOINT2);
    throwExceptionOnAction(engine2Spy, new IllegalArgumentException("runtime exception"));
    engine1Spy.markAsReadyForNewRequest();
    engine2Spy.markAsBooked();

    try {
      orchestrator.runOnEngine(requestId, ACTION, buildFunction());
      fail("should fail since engine threw an exception");

    } catch (Exception exception) {
      assertInstanceOf(IllegalArgumentException.class, exception);
      assertEquals("runtime exception", exception.getMessage());
    }
    verifyStates(IDLE, BOOKED);
  }

  @Test
  public void should_throw_exception_if_request_found_but_no_engine_with_info_when_running_on_engine() {
    long requestId = 7L;
    onGetRequest(requestId, UNAVAILABLE_ENDPOINT);
    engine1Spy.markAsReadyForNewRequest();
    engine2Spy.markAsReadyForNewRequest();

    try {
      orchestrator.runOnEngine(requestId, ACTION, buildFunction());
      fail("should fail since no engine with service info found");

    } catch (Exception exception) {
      assertOnNoEngineWithInformationException(exception);
    }
    verifyStates(IDLE, IDLE);
  }

  @Test
  public void should_retrieve_request_and_run_on_engine_with_request_id() {
    long requestId = 8L;
    onGetRequest(requestId, ENDPOINT2);
    doReturnOnAction(engine2Spy, buildTimeSeries());
    engine1Spy.markAsReadyForNewRequest();
    engine2Spy.markAsBooked();

    orchestrator.runOnEngine(requestId, ACTION, buildFunction());

    InOrder inOrder = buildInOrder();
    inOrder.verify(requestRegistry).get(requestId);
    inOrder.verify(engine2Spy).markAsStartingAction();
    inOrder.verify(engine2Spy).predict(any(TimeSeriesAnalysisRequest.class));
    inOrder.verify(engine2Spy).markAsActionEnded();
    inOrder.verifyNoMoreInteractions();
    verifyStates(IDLE, BOOKED);
  }

  @Test
  public void should_return_result_on_run_on_engine_with_request_id() {
    long requestId = 10L;
    onGetRequest(requestId, ENDPOINT2);
    doReturnOnAction(engine2Spy, buildTimeSeries());
    engine1Spy.markAsReadyForNewRequest();
    engine2Spy.markAsBooked();

    TimeSeries result = orchestrator.runOnEngine(requestId, ACTION, buildFunction());

    assertEquals(buildTimeSeries(), result);
    verifyStates(IDLE, BOOKED);
  }

  @Test
  public void should_return_completed_request() {
    long requestId = 11L;
    onGetRequest(requestId, ENDPOINT1);
    engine1Spy.markAsBooked();
    engine2Spy.markAsReadyForNewRequest();

    Optional<Request> actualCompletedRequest = orchestrator.completeRequest(requestId, ACTION);

    Request expectedRequest = new Request(requestId, ENDPOINT1);
    assertTrue(actualCompletedRequest.isPresent());
    assertEquals(expectedRequest, actualCompletedRequest.get());
    verifyStates(IDLE, IDLE);
  }

  @Test
  public void should_return_empty_optional_if_no_request_to_complete() {
    long requestId = 11L;
    onGetRequest(requestId, null);
    engine1Spy.markAsReadyForNewRequest();
    engine2Spy.markAsReadyForNewRequest();

    Optional<Request> actualCompletedRequest = orchestrator.completeRequest(requestId, ACTION);

    assertTrue(actualCompletedRequest.isEmpty());
    verifyStates(IDLE, IDLE);
  }

  @Test
  public void should_remove_request_and_mark_as_waiting_on_complete_request() {
    long requestId = 11L;
    onGetRequest(requestId, ENDPOINT1);
    engine1Spy.markAsBooked();
    engine2Spy.markAsReadyForNewRequest();

    orchestrator.completeRequest(requestId, ACTION);

    InOrder inOrder = buildInOrder();
    inOrder.verify(requestRegistry).get(requestId);
    inOrder.verify(engine1Spy).markAsReadyForNewRequest();
    inOrder.verify(requestRegistry).release(requestId);
    inOrder.verifyNoMoreInteractions();
    verifyStates(IDLE, IDLE);
  }

  @Test
  public void should_run_priority_on_engine_without_booking_the_engine() {
    onPingReturn(engine1Spy, buildEngineDetailResponse1());
    engine1Spy.markAsReadyForNewRequest();
    engine2Spy.markAsReadyForNewRequest();

    orchestrator.bookEngine(1L, "action1");
    orchestrator.bookEngine(2L, "action2");
    EngineDetailResponse actualResult = orchestrator.priorityRunOnEngine(0, "ping", AdminEngine::ping);

    InOrder inOrder = buildInOrder();
    inOrder.verify(engine1Spy).markAsReadyForNewRequest();
    inOrder.verify(engine2Spy).markAsReadyForNewRequest();
    inOrder.verify(engine1Spy).markAsBooked();
    inOrder.verify(engine2Spy).markAsBooked();
    inOrder.verify(engine1Spy).ping();
    inOrder.verifyNoMoreInteractions();
    verifyStates(BOOKED, BOOKED);
    assertEquals(buildEngineDetailResponse1(), actualResult);
  }

  @Test
  public void should_throw_exception_if_no_engine_with_id_on_run_priority_on_engine() {

    try {
      orchestrator.priorityRunOnEngine(3, "ping", AdminEngine::ping);
      fail("should fail since engineId 3 is not available");

    } catch (Exception exception) {
      assertInstanceOf(NoEngineWithInformationException.class, exception);
      assertEquals("No engine with id: `3` to run `ping`", exception.getMessage());
    }
  }

  @Test
  public void should_run_priority_on_engines_without_booking_the_engine() {
    onPingReturn(engine1Spy, buildEngineDetailResponse1());
    onPingReturn(engine2Spy, buildEngineDetailResponse2());
    engine1Spy.markAsReadyForNewRequest();
    engine2Spy.markAsReadyForNewRequest();

    orchestrator.bookEngine(1L, "action1");
    orchestrator.bookEngine(2L, "action2");
    EngineDetailResponse actualResult1 = orchestrator.priorityRunOnEngine(0, "ping", AdminEngine::ping);
    EngineDetailResponse actualResult2 = orchestrator.priorityRunOnEngine(1, "ping", AdminEngine::ping);

    InOrder inOrder = buildInOrder();
    inOrder.verify(engine1Spy).markAsReadyForNewRequest();
    inOrder.verify(engine2Spy).markAsReadyForNewRequest();
    inOrder.verify(engine1Spy).markAsBooked();
    inOrder.verify(engine2Spy).markAsBooked();
    inOrder.verify(engine1Spy).ping();
    inOrder.verify(engine2Spy).ping();
    inOrder.verifyNoMoreInteractions();
    verifyStates(BOOKED, BOOKED);
    assertEquals(buildEngineDetailResponse1(), actualResult1);
    assertEquals(buildEngineDetailResponse2(), actualResult2);
  }

  @Test
  public void should_run_priority_on_all_engines_without_booking_the_engine() {
    onPingReturn(engine1Spy, buildEngineDetailResponse1());
    onPingReturn(engine2Spy, buildEngineDetailResponse2());
    engine1Spy.markAsReadyForNewRequest();
    engine2Spy.markAsReadyForNewRequest();

    orchestrator.bookEngine(1L, "action1");
    orchestrator.bookEngine(2L, "action2");
    List<EngineDetailResponse> actualResult = orchestrator.priorityRunOnAllEngines("ping", AdminEngine::ping);

    InOrder inOrder = buildInOrder();
    inOrder.verify(engine1Spy).markAsReadyForNewRequest();
    inOrder.verify(engine2Spy).markAsReadyForNewRequest();
    inOrder.verify(engine1Spy).markAsBooked();
    inOrder.verify(engine2Spy).markAsBooked();
    inOrder.verify(engine1Spy).ping();
    inOrder.verify(engine2Spy).ping();
    inOrder.verifyNoMoreInteractions();
    verifyStates(BOOKED, BOOKED);
    assertEquals(newArrayList(buildEngineDetailResponse1(), buildEngineDetailResponse2()), actualResult);
  }

  private InOrder buildInOrder() {
    return inOrder(engine1Spy, engine2Spy, requestRegistry);
  }

  private void onGetRequest(long requestId, Endpoint endpoint) {
    Optional<Request> request = ofNullable(endpoint).map(info -> new Request(requestId, info));
    when(requestRegistry.get(requestId)).thenReturn(request);
  }

  private void onPingReturn(Engine engine, EngineDetailResponse engineDetailResponse) {
    doReturn(engineDetailResponse).when(engine).ping();
  }

  private static void doNothingOnLaunchEngine(Engine engine) {
    doNothing().when(engine).launchEngine(any());
  }

  private static void doThrowExceptionOnLaunchEngine(Engine engine) {
    doThrow(new RuntimeException("Exception while relaunch")).when(engine).launchEngine(any());
  }

  private void doReturnOnAction(Engine engine, TimeSeries timeSeries) {
    doReturn(timeSeries).when(engine).predict(any(TimeSeriesAnalysisRequest.class));
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

  private void verifyStates(EngineState engine1State, EngineState engine2State) {
    assertEquals(engine1State, engine1Spy.getState(), "engine1 state mismatch!");
    assertEquals(engine2State, engine2Spy.getState(), "engine2 state mismatch!");
  }

  private static TimeSeries buildTimeSeries() {
    TimeSeriesRow row1 = new TimeSeriesRow("date1", 123.);
    TimeSeriesRow row2 = new TimeSeriesRow("date2", 124.);
    return new TimeSeries(newArrayList(row1, row2), "date", "value", "yyyyMM");
  }

  private static Function<Engine, TimeSeries> buildFunction() {
    return engine -> engine.predict(mock(TimeSeriesAnalysisRequest.class));
  }

  private static EngineDetailResponse buildEngineDetailResponse1() {
    return new EngineDetailResponse(
      newArrayList(new ProcessDetailResponse(1, "state", 2, "startDatetime")),
      newArrayList()
    );
  }

  private static EngineDetailResponse buildEngineDetailResponse2() {
    return new EngineDetailResponse(
        newArrayList(new ProcessDetailResponse(2, "state2", 3, "startDatetime2")),
        newArrayList()
    );
  }
}