package org.mlsk.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.service.Engine;
import org.mlsk.service.impl.exceptions.NoAvailableEngineException;
import org.mlsk.service.model.EngineState;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
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

  @Test
  public void should_push_action_on_available_engine() {
    Orchestrator orchestrator = new Orchestrator(newArrayList(engine1, engine2));
    when(engine1.getState()).thenReturn(EngineState.COMPUTING);
    when(engine2.getState()).thenReturn(EngineState.WAITING);

    orchestrator.runOnEngine(engine -> engine.predict(mock(TimeSeriesAnalysisRequest.class)), "");

    verify(engine2).bookEngine();
    verify(engine1, never()).predict(any());
    verify(engine2, times(1)).predict(any());
  }

  @Test
  public void should_throw_exception_if_no_engine_is_available_for_action() {
    Orchestrator orchestrator = new Orchestrator(newArrayList(engine1, engine2));
    when(engine1.getState()).thenReturn(EngineState.COMPUTING);
    when(engine2.getState()).thenReturn(EngineState.COMPUTING);

    try {
      orchestrator.runOnEngine(engine -> engine.predict(mock(TimeSeriesAnalysisRequest.class)), "");
      fail("should fail because no engine is available");
    } catch (NoAvailableEngineException ignored) {
    }
  }
}