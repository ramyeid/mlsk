package org.mlsk.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.service.Engine;
import org.mlsk.service.impl.exceptions.NoAvailableEngineException;
import org.mlsk.service.model.EngineState;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class Orchestrator {

  private static final Logger LOGGER = LogManager.getLogger(Orchestrator.class);

  private final List<Engine> engines;

  public Orchestrator(List<Engine> engines) {
    this.engines = engines;
  }

  public <Result> Result runOnEngine(Function<Engine, Result> action, String actionName) {
    Engine availableEngine = retrieveAvailableEngine(actionName);

    return action.apply(availableEngine);
  }

  private synchronized Engine retrieveAvailableEngine(String actionName) {
    Optional<Engine> availableEngineOptional = this.engines.stream().filter(engine -> engine.getState().equals(EngineState.WAITING)).findFirst();

    if (availableEngineOptional.isEmpty()) {
      LOGGER.error(String.format("No Engine available to treat %s", actionName));
      throw new NoAvailableEngineException(actionName);
    }

    Engine availableEngine = availableEngineOptional.get();
    availableEngine.bookEngine();
    LOGGER.info(String.format("Request %s will be treated on engine: %s", actionName, availableEngine.getServiceInformation()));
    return availableEngine;
  }
}
