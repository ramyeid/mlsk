package org.mlsk.service.impl.orchestrator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.service.Engine;
import org.mlsk.service.impl.orchestrator.exception.NoAvailableEngineException;
import org.mlsk.service.model.EngineState;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrchestratorTest {

  @Mock
  private Engine engine1;
  @Mock
  private Engine engine2;

  private Orchestrator orchestrator;

  @BeforeEach
  public void setUp() {
    this.orchestrator = new Orchestrator(newArrayList(engine1, engine2));
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
  public void should_push_action_on_available_engine() {
    when(engine1.getState()).thenReturn(EngineState.COMPUTING);
    when(engine2.getState()).thenReturn(EngineState.WAITING);

    orchestrator.runOnEngine(engine -> engine.predict(mock(TimeSeriesAnalysisRequest.class)), "");

    InOrder inOrder = buildInOrder();
    inOrder.verify(engine2).bookEngine();
    inOrder.verify(engine1, never()).predict(any());
    inOrder.verify(engine2, times(1)).predict(any());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_exception_if_no_engine_is_available_for_action() {
    when(engine1.getState()).thenReturn(EngineState.COMPUTING);
    when(engine2.getState()).thenReturn(EngineState.COMPUTING);

    try {
      orchestrator.runOnEngine(engine -> engine.predict(mock(TimeSeriesAnalysisRequest.class)), "");
      fail("should fail because no engine is available");
    } catch (NoAvailableEngineException ignored) {
    }
  }

  private InOrder buildInOrder() {
    return inOrder(engine1, engine2);
  }
}