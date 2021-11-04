package org.mlsk.service.impl.orchestrator;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.service.engine.Engine;
import org.mlsk.service.impl.orchestrator.exception.NoAvailableEngineException;
import org.mlsk.service.impl.orchestrator.request.RequestIdGenerator;
import org.mlsk.service.impl.orchestrator.request.RequestIdRegistry;
import org.mlsk.service.model.engine.EngineState;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Collections.unmodifiableList;
import static org.mlsk.service.impl.orchestrator.exception.NoAvailableEngineException.*;

public class Orchestrator {

  private static final Logger LOGGER = LogManager.getLogger(Orchestrator.class);

  private final List<Engine> engines;
  private final RequestIdGenerator requestIdGenerator;
  private final RequestIdRegistry requestIdRegistry;

  public Orchestrator(List<Engine> engines) {
    this(engines, new RequestIdGenerator(), new RequestIdRegistry());
  }

  @VisibleForTesting
  public Orchestrator(List<Engine> engines, RequestIdGenerator requestIdGenerator, RequestIdRegistry requestIdRegistry) {
    this.engines = engines;
    this.requestIdGenerator = requestIdGenerator;
    this.requestIdRegistry = requestIdRegistry;
  }

  public List<Engine> getEngines() {
    return unmodifiableList(engines);
  }

  public void launchEngines() {
    this.engines.forEach(Engine::launchEngine);
  }

  public <Result> Result runOnEngine(Function<Engine, Result> action, String actionName) {
    Engine availableEngine = retrieveAvailableEngine(actionName);

    return callOnEngine(availableEngine, action, actionName, true);
  }

  public <Result> Pair<String, Result> runOnEngineAndBlock(Function<Engine, Result> action, String actionName) {
    Engine availableEngine = retrieveAvailableEngine(actionName);

    String requestId = requestIdGenerator.generateRequestId(availableEngine.getServiceInformation());
    requestIdRegistry.addRequestIdAndEngineInformation(requestId, availableEngine.getServiceInformation());

    return Pair.of(requestId, callOnEngine(availableEngine, action, actionName, false));
  }

  public <Result> Result runOnEngineAndBlock(String requestId, Function<Engine, Result> action, String actionName) {
    Engine availableEngine = retrieveEngine(requestId, actionName);

    return callOnEngine(availableEngine, action, actionName, false);
  }

  public <Result> Result runOnEngineAndUnblock(String requestId, Function<Engine, Result> action, String actionName) {
    Engine availableEngine = retrieveEngine(requestId, actionName);

    requestIdRegistry.removeRequestId(requestId);

    return callOnEngine(availableEngine, action, actionName, true);
  }

  private synchronized Engine retrieveAvailableEngine(String actionName) {
    Predicate<Engine> waitingEngine = engine -> engine.getState().equals(EngineState.WAITING);
    Engine availableEngine = getEngine(waitingEngine, buildNoAvailableEngineExceptionSupplier(actionName));

    availableEngine.bookEngine();
    LOGGER.info("Request {} will be treated on engine: {}", actionName, availableEngine.getServiceInformation());
    return availableEngine;
  }

  private synchronized Engine retrieveEngine(String requestId, String actionName) {
    Optional<ServiceInformation> engineInformationOptional = requestIdRegistry.getEngineInformation(requestId);
    ServiceInformation engineInformation = engineInformationOptional.orElseThrow(buildNoAvailableBlockedEngineExceptionSupplier(requestId, actionName));

    Predicate<Engine> engineWithInformation = engine -> engine.getServiceInformation().equals(engineInformation);
    Engine availableEngine = getEngine(engineWithInformation, buildNoEngineWithInformationExceptionSupplier(engineInformation, actionName));
    LOGGER.info("Request {} will be treated on engine: {}", actionName, availableEngine.getServiceInformation());
    return availableEngine;
  }

  private <Result> Result callOnEngine(Engine engine, Function<Engine, Result> action, String actionName, boolean unblockEngine) {
    synchronized (engine.getServiceInformation()) {
      try {
        LOGGER.info("Engine {} computing request: {}", engine.getServiceInformation(), actionName);
        engine.markAsComputing();
        return action.apply(engine);
      } catch (Exception exception) {
        LOGGER.error("Error while launching: {} on engine {}: {}", actionName, engine.getServiceInformation(), exception.getMessage());
        throw exception;
      } finally {
        resetEngineStatus(engine, actionName, unblockEngine);
      }
    }
  }

  private static void resetEngineStatus(Engine engine, String actionName, boolean unblockEngine) {
    if (unblockEngine) {
      LOGGER.info("Engine {} in waiting mode after computing request: {}", engine.getServiceInformation(), actionName);
      engine.markAsWaiting();
    } else {
      LOGGER.info("Engine {} in booked mode after computing request: {}", engine.getServiceInformation(), actionName);
      engine.bookEngine();
    }
  }

  private Engine getEngine(Predicate<Engine> predicate, Supplier<NoAvailableEngineException> exceptionSupplier) {
    return engines.stream().filter(predicate).findFirst().orElseThrow(exceptionSupplier);
  }

  private static Supplier<NoAvailableEngineException> buildNoAvailableEngineExceptionSupplier(String actionName) {
    return () -> {
      LOGGER.error("No engine available to treat {}", actionName);
      return buildNoAvailableEngineException(actionName);
    };
  }

  private static Supplier<NoAvailableEngineException> buildNoAvailableBlockedEngineExceptionSupplier(String requestId, String actionName) {
    return () -> {
      LOGGER.error("No engine blocked with id {} to treat {}", requestId, actionName);
      return buildNoAvailableBlockedEngineException(requestId, actionName);
    };
  }

  private static Supplier<NoAvailableEngineException> buildNoEngineWithInformationExceptionSupplier(ServiceInformation serviceInformation, String actionName) {
    return () -> {
      LOGGER.error("No engine found with Service Information {}, not expected - please check the logs!", serviceInformation);
      return buildNoEngineWithInformationException(serviceInformation, actionName);
    };
  }
}
