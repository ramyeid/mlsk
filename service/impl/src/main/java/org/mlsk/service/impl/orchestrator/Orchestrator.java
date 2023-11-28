package org.mlsk.service.impl.orchestrator;

import org.mlsk.service.engine.Engine;
import org.mlsk.service.impl.orchestrator.request.model.Request;
import org.mlsk.service.model.engine.EngineState;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface Orchestrator {

  EngineState getEngineState(int engineId);

  void launchEngines();

  <Result> Result priorityRunOnEngine(int engineId, Function<Engine, Result> action);

  <Result> List<Result> priorityRunOnAllEngines(Function<Engine, Result> action);

  /**
   * Acquire engine, run action and release lock on engine
   * Note: this is equivalent of {@link this#bookEngine}, {@link this#runOnEngine} and then {@link this#completeRequest}
   *
   * @param requestId  unique id to be assigned with an engine
   * @param actionName action id assigned to the current request to be launched
   * @param action     lambda function of the API to call on engine
   * @return result from Engine, result from `action`
   */
  default <Result> Result bookEngineRunAndComplete(long requestId, String actionName, Function<Engine, Result> action) {
    try {
      bookEngine(requestId, actionName);
      return runOnEngine(requestId, actionName, action);
    } finally {
      completeRequest(requestId, actionName);
    }
  }

  /**
   * Acquire an engine and assign requestId to this engine
   * This method will throw an exception if no engine is available.
   * <p>
   * Note: You can use {@link this#bookEngineRunAndComplete} if you are planning to launch a single action on the engine
   * If you plan to launch multiple actions for the same requestId, you can use this method and then call {@link this#runOnEngine} and finally {@link this#completeRequest}
   * <p>
   * Note: It is important to call {@link this#completeRequest} when the actions on engine are done in order to release resources acquired for this request
   *
   * @param requestId  unique id to be assigned with an engine
   * @param actionName action id assigned to be launched
   * @return Acquired engine which is linked to requestId
   */
  Engine bookEngine(long requestId, String actionName);

  /**
   * Run action on an already booked engine that is linked with `requestId`
   * <p>
   * Note: {@link this#bookEngine} needs to be called before calling this method, in order to acquire an engine and link it with the `requestId`
   *
   * @param requestId  unique id already linked with an engine
   * @param actionName action id to be launched
   * @param action     lambda function of the API to call on engine
   * @return result from Engine, result from `action`
   */
  <Result> Result runOnEngine(long requestId, String actionName, Function<Engine, Result> action);

  /**
   * Release link between requestId and engine
   * <p>
   * Note: Use {@link this#bookEngine} to acquire and book an engine to a requestId and {@link this#completeRequest} to clear out all resources allocated for this request
   *
   * @param requestId  unique id already linked with an engine, to be released
   * @param actionName action id assigned with the request
   * @return Released Request
   */
  Optional<Request> completeRequest(long requestId, String actionName);
}
