package org.mlsk.service.impl.orchestrator;

import org.mlsk.service.engine.Engine;

import java.util.List;
import java.util.function.Function;

public interface Orchestrator {

  List<Engine> getEngines();

  void launchEngines();

  String bookEngine(String actionName);

  <Result> Result runOnEngine(Function<Engine, Result> action, String actionName);

  <Result> Result runOnEngine(String requestId, Function<Engine, Result> action, String actionName);

  void releaseEngine(String requestId, String actionName);
}