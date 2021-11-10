package org.mlsk.service.impl.testhelper;

import org.mlsk.service.engine.Engine;
import org.mlsk.service.impl.orchestrator.Orchestrator;
import org.mockito.invocation.InvocationOnMock;

import java.util.Arrays;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public final class OrchestratorHelper {

  private OrchestratorHelper() {
  }

  public static void onBookEngineReturn(Orchestrator orchestrator, String requestId) {
    when(orchestrator.bookEngine(any())).thenReturn(requestId);
  }

  public static void onRunOnEngineCallMethod(Orchestrator orchestrator, Engine engine) {
    when(orchestrator.runOnEngine(any(), any()))
        .thenAnswer(invocation -> buildAnswer(engine, invocation));
  }

  public static void onRunOnEngineCallMethod(Orchestrator orchestrator, Engine engine, String requestId) {
    when(orchestrator.runOnEngine(eq(requestId), any(), any()))
        .thenAnswer(invocation -> buildAnswer(engine, invocation));
  }

  public static void doThrowExceptionOnRunOnEngine(Orchestrator orchestrator, Engine engine, String actionName, String exceptionMessage) {
    when(orchestrator.runOnEngine(any(), any()))
        .thenAnswer(invocation -> buildAnswerWithException(engine, actionName, exceptionMessage, invocation));
  }

  public static void doThrowExceptionOnRunOnEngine(Orchestrator orchestrator, Engine engine, String requestId, String actionName, String exceptionMessage) {
    when(orchestrator.runOnEngine(eq(requestId), any(), any()))
        .thenAnswer(invocation -> buildAnswerWithException(engine, actionName, exceptionMessage, invocation));
  }

  private static Object buildAnswerWithException(Engine engine, String actionName, String exceptionMessage, InvocationOnMock invocation) {
    Object result = buildAnswer(engine, invocation);
    if (invocation.getArgument(invocation.getArguments().length - 1).equals(actionName)) {
      throw new RuntimeException(exceptionMessage);
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private static Object buildAnswer(Engine engine, InvocationOnMock invocation) {
    Function<Engine, ?> function = (Function<Engine, ?>) Arrays.stream(invocation.getArguments()).filter(t -> t instanceof Function).findFirst().get();
    return function.apply(engine);
  }
}
