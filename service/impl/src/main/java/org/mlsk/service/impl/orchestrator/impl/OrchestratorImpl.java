package org.mlsk.service.impl.orchestrator.impl;

import com.google.common.annotations.VisibleForTesting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.lib.model.Endpoint;
import org.mlsk.service.engine.Engine;
import org.mlsk.service.impl.orchestrator.Orchestrator;
import org.mlsk.service.impl.orchestrator.exception.NoAvailableEngineException;
import org.mlsk.service.impl.orchestrator.exception.NoBlockedEngineException;
import org.mlsk.service.impl.orchestrator.exception.NoEngineWithInformationException;
import org.mlsk.service.impl.orchestrator.request.RequestHandler;
import org.mlsk.service.impl.orchestrator.request.model.Request;
import org.mlsk.service.model.engine.EngineState;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Collections.unmodifiableList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mlsk.service.impl.orchestrator.exception.NoAvailableEngineException.buildNoAvailableEngineException;
import static org.mlsk.service.impl.orchestrator.exception.NoBlockedEngineException.buildNoAvailableBlockedEngineException;
import static org.mlsk.service.impl.orchestrator.exception.NoEngineWithInformationException.buildNoEngineWithInformationException;

public class OrchestratorImpl implements Orchestrator {

  private static final Logger LOGGER = LogManager.getLogger(OrchestratorImpl.class);

  private final List<Engine> engines;
  private final RequestHandler requestHandler;

  public OrchestratorImpl(List<Engine> engines) {
    this(engines, new RequestHandler());
  }

  @VisibleForTesting
  public OrchestratorImpl(List<Engine> engines, RequestHandler requestHandler) {
    this.engines = engines;
    this.requestHandler = requestHandler;
  }

  @Override
  public List<Engine> getEngines() {
    return unmodifiableList(engines);
  }

  @Override
  public void launchEngines() {
    this.engines.forEach(Engine::launchEngine);
  }

  @Override
  public String bookEngine(String actionName) {
    LOGGER.info("Searching for engine to book for {}", actionName);
    Engine availableEngine = retrieveAvailableEngineAndBook(actionName);
    Endpoint endpoint = availableEngine.getEndpoint();
    LOGGER.info("Engine {} will treat action {}", endpoint, actionName);

    String requestId = requestHandler.registerNewRequest(actionName, endpoint);
    LOGGER.info("Request Id {} is assigned to engine {} with action {}", requestId, endpoint, actionName);

    return requestId;
  }

  @Override
  public <Result> Result runOnEngine(Function<Engine, Result> action, String actionName) {
    Optional<String> requestIdOptional = empty();
    try {
      requestIdOptional = of(bookEngine(actionName));
      Engine availableEngine = retrieveEngine(requestIdOptional.get(), actionName);
      return callOnEngine(availableEngine, requestIdOptional.get(), action, actionName);
    } finally {
      requestIdOptional.ifPresent(requestId -> releaseEngine(requestId, actionName));
    }
  }

  @Override
  public <Result> Result runOnEngine(String requestId, Function<Engine, Result> action, String actionName) {
    Engine availableEngine = retrieveEngine(requestId, actionName);

    return callOnEngine(availableEngine, requestId, action, actionName);
  }

  @Override
  public void releaseEngine(String requestId, String actionName) {
    Optional<Request> requestOptional = requestHandler.getRequest(requestId);
    Request requestToRemove = requestOptional.orElseThrow(buildNoAvailableBlockedEngineExceptionSupplier(requestId, actionName));

    removeRequestAndMarkAsWaiting(requestId, actionName, requestToRemove);
  }

  private synchronized Engine retrieveAvailableEngineAndBook(String actionName) {
    Predicate<Engine> waitingEngine = engine -> engine.getState().equals(EngineState.WAITING);
    Engine availableEngine = getEngine(waitingEngine, buildNoAvailableEngineExceptionSupplier(actionName));

    availableEngine.bookEngine();
    return availableEngine;
  }

  private Engine retrieveEngine(String requestId, String actionName) {
    Optional<Request> requestOptional = requestHandler.getRequest(requestId);
    Request request = requestOptional.orElseThrow(buildNoAvailableBlockedEngineExceptionSupplier(requestId, actionName));

    Predicate<Engine> engineWithInformation = engine -> engine.getEndpoint().equals(request.getEndpoint());
    return getEngine(engineWithInformation, buildNoEngineWithInformationExceptionSupplier(request.getEndpoint(), actionName));
  }

  private void removeRequestAndMarkAsWaiting(String requestId, String actionName, Request requestToRemove) {
    synchronized (requestToRemove.getEndpoint()) {
      LOGGER.info("[{}] Engine {} in waiting mode after computing action: {}", requestId, requestToRemove.getEndpoint(), actionName);
      retrieveEngine(requestId, actionName).markAsWaitingForRequest();
      LOGGER.info("[{}] Releasing engine {} with action {}", requestId, requestToRemove.getEndpoint(), actionName);
      requestHandler.removeRequest(requestId);
    }
  }

  private <Result> Result callOnEngine(Engine engine, String requestId, Function<Engine, Result> action, String actionName) {
    synchronized (engine.getEndpoint()) {
      try {
        LOGGER.info("[{}] Engine {} computing action: {}", requestId, engine.getEndpoint(), actionName);
        engine.markAsComputing();
        return action.apply(engine);
      } catch (Exception exception) {
        LOGGER.error("[{}] Error while launching: {} on engine {}: {}", requestId, actionName, engine.getEndpoint(), exception.getMessage());
        throw exception;
      } finally {
        LOGGER.info("[{}] Engine {} reset to booked mode after computing action: {}", requestId, engine.getEndpoint(), actionName);
        engine.bookEngine();
      }
    }
  }

  private Engine getEngine(Predicate<Engine> predicate, Supplier<? extends RuntimeException> exceptionSupplier) {
    return engines.stream().filter(predicate).findFirst().orElseThrow(exceptionSupplier);
  }

  private static Supplier<NoAvailableEngineException> buildNoAvailableEngineExceptionSupplier(String actionName) {
    return () -> {
      LOGGER.error("No engine available to treat {}", actionName);
      return buildNoAvailableEngineException(actionName);
    };
  }

  private static Supplier<NoBlockedEngineException> buildNoAvailableBlockedEngineExceptionSupplier(String requestId, String actionName) {
    return () -> {
      LOGGER.error("No engine blocked with id {} to treat {}", requestId, actionName);
      return buildNoAvailableBlockedEngineException(requestId, actionName);
    };
  }

  private static Supplier<NoEngineWithInformationException> buildNoEngineWithInformationExceptionSupplier(Endpoint endpoint, String actionName) {
    return () -> {
      LOGGER.error("No engine found with Endpoint {}, not expected - please check the logs!", endpoint);
      return buildNoEngineWithInformationException(endpoint, actionName);
    };
  }
}
