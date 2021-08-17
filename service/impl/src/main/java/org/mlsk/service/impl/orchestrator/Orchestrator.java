package org.mlsk.service.impl.orchestrator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.service.engine.Engine;
import org.mlsk.service.impl.orchestrator.exception.NoAvailableEngineException;
import org.mlsk.service.model.engine.EngineState;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Collections.unmodifiableList;

public class Orchestrator {

  private static final Logger LOGGER = LogManager.getLogger(Orchestrator.class);

  private final List<Engine> engines;

  public Orchestrator(List<Engine> engines) {
    this.engines = engines;
  }

  public void launchEngines() {
    this.engines.forEach(Engine::launchEngine);
  }

  public List<Engine> getEngines() {
    return unmodifiableList(engines);
  }

  public <Result> Result runOnEngine(Function<Engine, Result> action, String actionName) {
    Engine availableEngine = retrieveAvailableEngine(actionName);

    return action.apply(availableEngine);
  }

  private synchronized Engine retrieveAvailableEngine(String actionName) {
    Optional<Engine> availableEngineOptional = this.engines.stream().filter(engine -> engine.getState().equals(EngineState.WAITING)).findFirst();

    if (availableEngineOptional.isEmpty()) {
      LOGGER.error("No Engine available to treat {}", actionName);
      throw new NoAvailableEngineException(actionName);
    }

    Engine availableEngine = availableEngineOptional.get();
    availableEngine.bookEngine();
    LOGGER.info("Request {} will be treated on engine: {}", actionName, availableEngine.getServiceInformation());
    return availableEngine;
  }
}
