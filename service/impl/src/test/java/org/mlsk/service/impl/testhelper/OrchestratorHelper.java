package org.mlsk.service.impl.testhelper;

import org.mlsk.service.engine.Engine;
import org.mlsk.service.impl.orchestrator.Orchestrator;
import org.mockito.invocation.InvocationOnMock;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public final class OrchestratorHelper {

  private OrchestratorHelper() {
  }

  public static void onPriorityRunOnEngine(Orchestrator orchestrator, Engine engine, int engineId) {
    when(orchestrator.priorityRunOnEngine(eq(engineId), any(), any()))
        .thenAnswer(invocation -> buildAnswer(engine, invocation));
  }

  public static void onPriorityRunOnAllEngines(Orchestrator orchestrator, List<Engine> engines) {
    when(orchestrator.priorityRunOnAllEngines(anyString(), any()))
        .thenAnswer(invocation -> engines.stream()
            .map(engine -> buildAnswer(engine, invocation))
            .collect(Collectors.toList()));
  }

  public static void onBookEngineReturn(Orchestrator orchestrator, Engine engine, long requestId) {
    when(orchestrator.bookEngine(eq(requestId), anyString())).thenReturn(engine);
  }

  public static void onBookEngineRunAndComplete(Orchestrator orchestrator, Engine engine, long requestId) {
    when(orchestrator.bookEngineRunAndComplete(eq(requestId), anyString(), any()))
        .thenAnswer(invocation -> buildAnswer(engine, invocation));
  }

  public static void onRunOnEngineCallMethod(Orchestrator orchestrator, Engine engine, long requestId) {
    when(orchestrator.runOnEngine(eq(requestId), anyString(), any()))
        .thenAnswer(invocation -> buildAnswer(engine, invocation));
  }

  public static void doThrowExceptionOnCompleteRequest(Orchestrator orchestrator, long requestId, String actionName, RuntimeException exception) {
    doThrow(exception).when(orchestrator).completeRequest(requestId, actionName);
  }

  public static void doThrowExceptionOnBookEngineRunAndComplete(Orchestrator orchestrator, Engine engine, String actionName, String exceptionMessage) {
    when(orchestrator.bookEngineRunAndComplete(anyLong(), anyString(), any()))
        .thenAnswer(invocation -> buildAnswerWithException(engine, actionName, new RuntimeException(exceptionMessage), invocation));
  }

  public static void doThrowExceptionOnRunOnEngine(Orchestrator orchestrator, Engine engine, long requestId, String actionName, String exceptionMessage) {
    when(orchestrator.runOnEngine(eq(requestId), any(), any()))
        .thenAnswer(invocation -> buildAnswerWithException(engine, actionName, new RuntimeException(exceptionMessage), invocation));
  }

  public static void doThrowExceptionOnPriorityRunOnEngine(Orchestrator orchestrator, Engine engine, String actionName, int engineId, String exceptionMessage) {
    when(orchestrator.priorityRunOnEngine(eq(engineId), eq(actionName), any()))
        .thenAnswer(invocation -> buildAnswerWithException(engine, actionName, new RuntimeException(exceptionMessage), invocation));
  }

  public static void doThrowExceptionOnPriorityRunOnAllEngines(Orchestrator orchestrator, Engine engine, String actionName, String exceptionMessage) {
    when(orchestrator.priorityRunOnAllEngines(eq(actionName), any()))
        .thenAnswer(invocation -> buildAnswerWithException(engine, actionName, new RuntimeException(exceptionMessage), invocation));
  }

  private static Object buildAnswerWithException(Engine engine, String actionName, RuntimeException exception, InvocationOnMock invocation) {
    Object result = buildAnswer(engine, invocation);
    String actualActionName = null;
    if (invocation.getArgument(0) instanceof String) {
      actualActionName = invocation.getArgument(0, String.class);
    } else if (invocation.getArgument(1) instanceof String) {
      actualActionName = invocation.getArgument(1, String.class);
    }
    if (actionName.equals(actualActionName)) {
      throw exception;
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private static Object buildAnswer(Engine engine, InvocationOnMock invocation) {
    Function<Engine, ?> function = (Function<Engine, ?>) Arrays.stream(invocation.getArguments()).filter(t -> t instanceof Function).findFirst().get();
    return function.apply(engine);
  }
}
