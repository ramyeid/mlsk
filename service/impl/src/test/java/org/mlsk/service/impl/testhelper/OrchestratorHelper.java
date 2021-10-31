package org.mlsk.service.impl.testhelper;

import org.apache.commons.lang3.tuple.Pair;
import org.mlsk.service.engine.Engine;
import org.mlsk.service.impl.orchestrator.Orchestrator;
import org.mockito.invocation.InvocationOnMock;

import java.util.Arrays;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public final class OrchestratorHelper {

  private OrchestratorHelper() {
  }

  public static void onRunOnEngineCallMethod(Orchestrator orchestrator, Engine engine) {
    when(orchestrator.runOnEngine(any(), any()))
        .thenAnswer(invocation -> buildAnswer(engine, invocation));
  }

  public static void onRunOnEngineAndBlockCallMethod(Orchestrator orchestrator, Engine engine, String requestId) {
    when(orchestrator.runOnEngineAndBlock(any(), any()))
        .thenAnswer(invocation -> Pair.of(requestId, buildAnswer(engine, invocation)));
  }

  public static void onRunOnEngineAndBlockWithIdCallMethod(Orchestrator orchestrator, Engine engine, String requestId) {
    when(orchestrator.runOnEngineAndBlock(eq(requestId), any(), any()))
        .thenAnswer(invocation -> buildAnswer(engine, invocation));
  }

  public static void onRunOnEngineAndUnblockCallMethod(Orchestrator orchestrator, Engine engine, String requestId) {
    when(orchestrator.runOnEngineAndUnblock(eq(requestId), any(), any()))
        .thenAnswer(invocation -> buildAnswer(engine, invocation));
  }

  public static void doThrowExceptionOnRunOnEngine(Orchestrator orchestrator, String exceptionMessage) {
    doThrow(new RuntimeException(exceptionMessage)).when(orchestrator).runOnEngine(any(), any());
  }

  public static void doThrowExceptionOnRunOnEngineAndBlock(Orchestrator orchestrator, String exceptionMessage) {
    doThrow(new RuntimeException(exceptionMessage)).when(orchestrator).runOnEngineAndBlock(any(), any());
  }

  public static void doThrowExceptionOnRunOnEngineAndBlockWithId(Orchestrator orchestrator, String requestId, String exceptionMessage) {
    doThrow(new RuntimeException(exceptionMessage)).when(orchestrator).runOnEngineAndBlock(eq(requestId), any(), any());
  }

  public static void doThrowExceptionOnRunOnEngineAndUnblock(Orchestrator orchestrator, String requestId, String exceptionMessage) {
    doThrow(new RuntimeException(exceptionMessage)).when(orchestrator).runOnEngineAndUnblock(eq(requestId), any(), any());
  }

  @SuppressWarnings("unchecked")
  private static Object buildAnswer(Engine engine, InvocationOnMock invocation) {
    Function<Engine, ?> function = (Function<Engine, ?>) Arrays.stream(invocation.getArguments()).filter(t -> t instanceof Function).findFirst().get();
    return function.apply(engine);
  }
}
