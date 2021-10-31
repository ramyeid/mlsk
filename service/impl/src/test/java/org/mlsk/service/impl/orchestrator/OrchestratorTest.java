package org.mlsk.service.impl.orchestrator;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.service.engine.Engine;
import org.mlsk.service.impl.orchestrator.exception.NoAvailableEngineException;
import org.mlsk.service.impl.orchestrator.request.RequestIdGenerator;
import org.mlsk.service.impl.orchestrator.request.RequestIdRegistry;
import org.mlsk.service.model.engine.EngineState;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.model.timeseries.TimeSeriesRow;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
public class OrchestratorTest {

  private static final ServiceInformation SERVICE_INFO1 = new ServiceInformation("host1", 123L);
  private static final ServiceInformation SERVICE_INFO2 = new ServiceInformation("host2", 1234L);
  private static final ServiceInformation UNAVAILABLE_SERVICE_INFO = new ServiceInformation("unavailable", 12345L);
  private static final String ACTION = "ACTION";

  @Mock
  private Engine engine1;
  @Mock
  private Engine engine2;
  @Mock
  private RequestIdGenerator requestIdGenerator;
  @Mock
  private RequestIdRegistry requestIdRegistry;

  private Orchestrator orchestrator;

  @BeforeEach
  public void setUp() {
    this.orchestrator = new Orchestrator(newArrayList(engine1, engine2), requestIdGenerator, requestIdRegistry);
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
  public void should_push_action_on_available_engine_on_run_on_engine() {
    onGetServiceInformationReturn(engine2, SERVICE_INFO2);
    onGetStateReturn(engine1, COMPUTING);
    onGetStateReturn(engine2, WAITING);

    orchestrator.runOnEngine(buildFunction(), ACTION);

    InOrder inOrder = buildInOrder();
    inOrder.verify(engine2).bookEngine();
    inOrder.verify(engine2).markAsComputing();
    inOrder.verify(engine2).predict(any(TimeSeriesAnalysisRequest.class));
    inOrder.verify(engine2).getServiceInformation();
    inOrder.verify(engine2).markAsWaiting();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_result_on_run_on_engine() {
    onGetServiceInformationReturn(engine2, SERVICE_INFO2);
    onGetStateReturn(engine1, COMPUTING);
    onGetStateReturn(engine2, WAITING);
    doReturnOnAction(engine2, buildTimeSeries());

    TimeSeries result = orchestrator.runOnEngine(buildFunction(), ACTION);

    assertEquals(buildTimeSeries(), result);
  }

  @Test
  public void should_push_action_on_available_engine_and_unblock_if_error_on_run_on_engine() {
    onGetServiceInformationReturn(engine2, SERVICE_INFO2);
    onGetStateReturn(engine1, COMPUTING);
    onGetStateReturn(engine2, WAITING);
    throwExceptionOnAction(engine2, new RuntimeException("exception"));

    try {
      orchestrator.runOnEngine(buildFunction(), ACTION);
      fail("should fail since action threw an error");
    } catch (Exception ignored) {
    }

    InOrder inOrder = buildInOrder();
    inOrder.verify(engine2).bookEngine();
    inOrder.verify(engine2).markAsComputing();
    inOrder.verify(engine2).predict(any(TimeSeriesAnalysisRequest.class));
    inOrder.verify(engine2, times(2)).getServiceInformation();
    inOrder.verify(engine2).markAsWaiting();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_exception_if_no_engine_is_available_for_action_on_run_engine_and_block() {
    onGetStateReturn(engine1, COMPUTING);
    onGetStateReturn(engine2, COMPUTING);

    try {
      orchestrator.runOnEngineAndBlock(buildFunction(), ACTION);
      fail("should fail because no engine is available");

    } catch (Exception exception) {
      assertOnNoAvailableEngineException(exception);
    }
  }

  @Test
  public void should_push_action_on_available_engine_on_run_on_engine_and_block() {
    onGetServiceInformationReturn(engine2, SERVICE_INFO2);
    onGetStateReturn(engine1, COMPUTING);
    onGetStateReturn(engine2, WAITING);
    onGenerateRequestIdReturn(SERVICE_INFO2, "requestId2");

    orchestrator.runOnEngineAndBlock(buildFunction(), ACTION);

    InOrder inOrder = buildInOrder();
    inOrder.verify(engine2).bookEngine();
    inOrder.verify(requestIdGenerator).generateRequestId(SERVICE_INFO2);
    inOrder.verify(requestIdRegistry).addRequestIdAndEngineInformation("requestId2", SERVICE_INFO2);
    inOrder.verify(engine2).markAsComputing();
    inOrder.verify(engine2).predict(any(TimeSeriesAnalysisRequest.class));
    inOrder.verify(engine2).getServiceInformation();
    inOrder.verify(engine2).bookEngine();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_result_on_run_on_engine_and_block() {
    onGetServiceInformationReturn(engine2, SERVICE_INFO2);
    onGetStateReturn(engine1, COMPUTING);
    onGetStateReturn(engine2, WAITING);
    doReturnOnAction(engine2, buildTimeSeries());
    onGenerateRequestIdReturn(SERVICE_INFO2, "requestId2");

    Pair<String, TimeSeries> result = orchestrator.runOnEngineAndBlock(buildFunction(), ACTION);

    assertEquals(buildTimeSeries(), result.getRight());
    assertEquals("requestId2", result.getLeft());
  }

  @Test
  public void should_push_action_on_available_engine_if_error_on_run_on_engine_and_block() {
    onGetServiceInformationReturn(engine2, SERVICE_INFO2);
    onGetStateReturn(engine1, COMPUTING);
    onGetStateReturn(engine2, WAITING);
    throwExceptionOnAction(engine2, new RuntimeException("exception"));
    onGenerateRequestIdReturn(SERVICE_INFO2, "requestId2");

    try {
      orchestrator.runOnEngineAndBlock(buildFunction(), ACTION);
      fail("should fail since action threw an error");
    } catch (Exception ignored) {
    }

    InOrder inOrder = buildInOrder();
    inOrder.verify(engine2).bookEngine();
    inOrder.verify(requestIdGenerator).generateRequestId(SERVICE_INFO2);
    inOrder.verify(requestIdRegistry).addRequestIdAndEngineInformation("requestId2", SERVICE_INFO2);
    inOrder.verify(engine2).markAsComputing();
    inOrder.verify(engine2).predict(any(TimeSeriesAnalysisRequest.class));
    inOrder.verify(engine2, times(2)).getServiceInformation();
    inOrder.verify(engine2).bookEngine();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_exception_if_no_engine_is_available_for_action_on_run_engine_and_block_with_request_id() {
    String requestId = "unavailableRequestId";

    try {
      orchestrator.runOnEngineAndBlock(requestId, buildFunction(), ACTION);
      fail("should fail because no engine booked is available");

    } catch (Exception exception) {
      assertOnNoAvailableBlockedEngineException(exception, requestId);
    }
  }


  @Test
  public void should_throw_exception_if_no_engine_with_information_available_for_action_on_run_engine_and_block_with_request_id() {
    String requestId = "unavailableRequestId";
    onGetServiceInformationReturn(engine1, SERVICE_INFO1);
    onGetServiceInformationReturn(engine2, SERVICE_INFO2);
    onGetEngineInformation(requestId, UNAVAILABLE_SERVICE_INFO);

    try {
      orchestrator.runOnEngineAndBlock(requestId, buildFunction(), ACTION);
      fail("should fail because no engine booked is available");

    } catch (Exception exception) {
      assertOnNoEngineWithInformationException(exception);
    }
  }

  @Test
  public void should_push_action_on_available_engine_on_run_on_engine_and_block_with_request_id() {
    String requestId = "requestId2";
    onGetServiceInformationReturn(engine1, SERVICE_INFO1);
    onGetServiceInformationReturn(engine2, SERVICE_INFO2);
    onGetEngineInformation(requestId, SERVICE_INFO2);

    orchestrator.runOnEngineAndBlock(requestId, buildFunction(), ACTION);

    InOrder inOrder = buildInOrder();
    inOrder.verify(requestIdRegistry).getEngineInformation(requestId);
    inOrder.verify(engine2).markAsComputing();
    inOrder.verify(engine2).predict(any(TimeSeriesAnalysisRequest.class));
    inOrder.verify(engine2).getServiceInformation();
    inOrder.verify(engine2).bookEngine();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_result_on_run_on_engine_and_block_with_request_id() {
    String requestId = "requestId2";
    onGetServiceInformationReturn(engine1, SERVICE_INFO1);
    onGetServiceInformationReturn(engine2, SERVICE_INFO2);
    onGetEngineInformation(requestId, SERVICE_INFO2);
    doReturnOnAction(engine2, buildTimeSeries());

    TimeSeries result = orchestrator.runOnEngineAndBlock(requestId, buildFunction(), ACTION);

    assertEquals(buildTimeSeries(), result);
  }

  @Test
  public void should_push_action_on_available_engine_if_error_on_run_on_engine_and_block_with_request_id() {
    String requestId = "requestId2";
    onGetServiceInformationReturn(engine1, SERVICE_INFO1);
    onGetServiceInformationReturn(engine2, SERVICE_INFO2);
    onGetEngineInformation(requestId, SERVICE_INFO2);
    throwExceptionOnAction(engine2, new RuntimeException("exception"));

    try {
      orchestrator.runOnEngineAndBlock(requestId, buildFunction(), ACTION);
      fail("should fail since action threw an error");
    } catch (Exception ignored) {
    }

    InOrder inOrder = buildInOrder();
    inOrder.verify(requestIdRegistry).getEngineInformation(requestId);
    inOrder.verify(engine2).markAsComputing();
    inOrder.verify(engine2).predict(any(TimeSeriesAnalysisRequest.class));
    inOrder.verify(engine2, times(2)).getServiceInformation();
    inOrder.verify(engine2).bookEngine();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_exception_if_no_engine_is_available_for_action_on_run_engine_and_unblock_with_request_id() {
    String requestId = "unavailableRequestId";

    try {
      orchestrator.runOnEngineAndUnblock(requestId, buildFunction(), ACTION);
      fail("should fail because no engine booked is available");

    } catch (Exception exception) {
      assertOnNoAvailableBlockedEngineException(exception, requestId);
    }
  }

  @Test
  public void should_throw_exception_if_no_engine_with_information_available_for_action_on_run_engine_and_unblock_with_request_id() {
    String requestId = "unavailableRequestId";
    onGetServiceInformationReturn(engine1, SERVICE_INFO1);
    onGetServiceInformationReturn(engine2, SERVICE_INFO2);
    onGetEngineInformation(requestId, UNAVAILABLE_SERVICE_INFO);

    try {
      orchestrator.runOnEngineAndUnblock(requestId, buildFunction(), ACTION);
      fail("should fail because no engine booked is available");

    } catch (Exception exception) {
      assertOnNoEngineWithInformationException(exception);
    }
  }

  @Test
  public void should_push_action_on_available_engine_on_run_on_engine_and_unblock_with_request_id() {
    String requestId = "requestId2";
    onGetServiceInformationReturn(engine1, SERVICE_INFO1);
    onGetServiceInformationReturn(engine2, SERVICE_INFO2);
    onGetEngineInformation(requestId, SERVICE_INFO2);

    orchestrator.runOnEngineAndUnblock(requestId, buildFunction(), ACTION);

    InOrder inOrder = buildInOrder();
    inOrder.verify(requestIdRegistry).getEngineInformation(requestId);
    inOrder.verify(requestIdRegistry).removeRequestId(requestId);
    inOrder.verify(engine2).markAsComputing();
    inOrder.verify(engine2).predict(any(TimeSeriesAnalysisRequest.class));
    inOrder.verify(engine2).getServiceInformation();
    inOrder.verify(engine2).markAsWaiting();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_result_on_run_on_engine_and_unblock_with_request_id() {
    String requestId = "requestId2";
    onGetServiceInformationReturn(engine1, SERVICE_INFO1);
    onGetServiceInformationReturn(engine2, SERVICE_INFO2);
    onGetEngineInformation(requestId, SERVICE_INFO2);
    doReturnOnAction(engine2, buildTimeSeries());

    TimeSeries result = orchestrator.runOnEngineAndUnblock(requestId, buildFunction(), ACTION);

    assertEquals(buildTimeSeries(), result);
  }

  @Test
  public void should_push_action_on_available_engine_if_error_on_run_on_engine_and_unblock_with_request_id() {
    String requestId = "requestId1";
    onGetServiceInformationReturn(engine1, SERVICE_INFO1);
    onGetServiceInformationReturn(engine2, SERVICE_INFO2);
    onGetEngineInformation(requestId, SERVICE_INFO2);
    throwExceptionOnAction(engine2, new RuntimeException("exception"));

    try {
      orchestrator.runOnEngineAndUnblock(requestId, buildFunction(), ACTION);
      fail("should fail since action threw an error");
    } catch (Exception ignored) {
    }

    InOrder inOrder = buildInOrder();
    inOrder.verify(requestIdRegistry).getEngineInformation(requestId);
    inOrder.verify(engine2).markAsComputing();
    inOrder.verify(engine2).predict(any(TimeSeriesAnalysisRequest.class));
    inOrder.verify(engine2, times(2)).getServiceInformation();
    inOrder.verify(engine2).markAsWaiting();
    inOrder.verifyNoMoreInteractions();
  }

  private InOrder buildInOrder() {
    return inOrder(engine1, engine2, requestIdGenerator, requestIdRegistry);
  }

  private static void onGetServiceInformationReturn(Engine engine, ServiceInformation serviceInformation) {
    when(engine.getServiceInformation()).thenReturn(serviceInformation);
  }

  private void onGetStateReturn(Engine engine, EngineState computing) {
    when(engine.getState()).thenReturn(computing);
  }

  private void onGenerateRequestIdReturn(ServiceInformation serviceInfo, String requestId) {
    when(requestIdGenerator.generateRequestId(serviceInfo)).thenReturn(requestId);
  }

  private void onGetEngineInformation(String requestId, ServiceInformation serviceInformation) {
    when(requestIdRegistry.getEngineInformation(requestId)).thenReturn(ofNullable(serviceInformation));
  }

  private void doReturnOnAction(Engine engine, TimeSeries timeSeries) {
    when(engine.predict(any(TimeSeriesAnalysisRequest.class))).thenReturn(timeSeries);
  }

  private static void throwExceptionOnAction(Engine engine, Exception exception) {
    doThrow(exception).when(engine).predict(any(TimeSeriesAnalysisRequest.class));
  }

  private static Function<Engine, TimeSeries> buildFunction() {
    return engine -> engine.predict(mock(TimeSeriesAnalysisRequest.class));
  }

  private static TimeSeries buildTimeSeries() {
    TimeSeriesRow row1 = new TimeSeriesRow("date1", 123.);
    TimeSeriesRow row2 = new TimeSeriesRow("date2", 124.);
    return new TimeSeries(newArrayList(row1, row2), "date", "value", "yyyyMM");
  }

  private static void assertOnNoAvailableEngineException(Exception exception) {
    assertInstanceOf(NoAvailableEngineException.class, exception);
    assertEquals(format("No available engine to run %s, please try again later", ACTION), exception.getMessage());
  }

  private void assertOnNoAvailableBlockedEngineException(Exception exception, String requestId) {
    assertInstanceOf(NoAvailableEngineException.class, exception);
    assertEquals(format("No available engine with %s to run %s", requestId, ACTION), exception.getMessage());
  }

  private void assertOnNoEngineWithInformationException(Exception exception) {
    assertInstanceOf(NoAvailableEngineException.class, exception);
    assertEquals(format("No engine found with information ServiceInformation{host='unavailable', port='12345'} to run %s - NOT EXPECTED - check logs!", ACTION), exception.getMessage());
  }
}