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
import org.mlsk.service.impl.orchestrator.request.model.Request;
import org.mlsk.service.impl.orchestrator.request.registry.RequestRegistry;
import org.mlsk.service.model.engine.EngineState;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static org.mlsk.service.impl.orchestrator.exception.NoAvailableEngineException.buildNoAvailableEngineException;
import static org.mlsk.service.impl.orchestrator.exception.NoBlockedEngineException.buildNoAvailableBlockedEngineException;
import static org.mlsk.service.impl.orchestrator.exception.NoEngineWithInformationException.buildNoEngineWithInformationException;

public class OrchestratorImpl implements Orchestrator {

  private static final Logger LOGGER = LogManager.getLogger(OrchestratorImpl.class);

  private final List<Engine> engines;
  private final RequestRegistry requestRegistry;

  public OrchestratorImpl(List<Engine> engines) {
    this(engines, new RequestRegistry());
  }

  @VisibleForTesting
  public OrchestratorImpl(List<Engine> engines, RequestRegistry requestRegistry) {
    this.engines = engines;
    this.requestRegistry = requestRegistry;
  }

  @Override
  public List<Engine> getEngines() {
    return unmodifiableList(engines);
  }

  @Override
  public void launchEngines() {
    this.engines.forEach(engine -> {
      try {
        engine.launchEngine(() -> this.onEngineKilled(engine));
        engine.markAsReadyForNewRequest();
      } catch (Exception exception) {
        engine.markAsNotAvailable();
        throw exception;
      }
    });
  }

  @Override
  public Engine bookEngine(long requestId, String actionName) {
    LOGGER.info("Searching for engine to book for {}", actionName);
    Engine availableEngine = retrieveAvailableEngineAndBook(actionName);

    Endpoint endpoint = availableEngine.getEndpoint();
    LOGGER.info("Engine {} will treat action {}", endpoint, actionName);

    requestRegistry.register(requestId, endpoint);
    LOGGER.info("Request Id {} is assigned to engine {} with action {}", requestId, endpoint, actionName);

    return availableEngine;
  }

  @Override
  public <Result> Result runOnEngine(long requestId, String actionName, Function<Engine, Result> action) {
    Optional<Request> requestOptional = requestRegistry.get(requestId);
    Request request = requestOptional.orElseThrow(buildNoAvailableBlockedEngineExceptionSupplier(requestId, actionName));

    Engine bookedEngine = retrieveBookedEngine(request, actionName);

    return callOnEngine(request, bookedEngine, actionName, action);
  }

  @Override
  public Optional<Request> completeRequest(long requestId, String actionName) {
    Optional<Request> requestToComplete = requestRegistry.get(requestId);
    if (requestToComplete.isEmpty()) {
      LOGGER.info("No inflight request found for id {}", requestId);
    } else {
      removeRequestAndMarkAsWaiting(requestToComplete.get(), actionName);
    }
    return requestToComplete;
  }

  @VisibleForTesting
  void onEngineKilled(Engine engine) {
    LOGGER.error("Engine {} died unexpectedly ", engine.getEndpoint());
    LOGGER.info("Relaunching engine {}", engine.getEndpoint());
    engine.markAsNotAvailable();
    this.requestRegistry.releaseAll(engine.getEndpoint());
    try {
      engine.launchEngine(() -> this.onEngineKilled(engine));
      engine.markAsReadyForNewRequest();
    } catch (Exception exception) {
      LOGGER.error(format("Error while relaunching engine: %s", engine.getEndpoint()), exception);
      engine.markAsNotAvailable();
    }
  }

  private synchronized Engine retrieveAvailableEngineAndBook(String actionName) {
    int maxRetries = 2;
    for (int i = 0; i < maxRetries; ++i) {
      Predicate<Engine> waitingEngine = engine -> engine.getState().equals(EngineState.IDLE);
      Engine availableEngine = getEngine(waitingEngine, buildNoAvailableEngineExceptionSupplier(actionName));

      if (availableEngine.markAsBooked()) {
        return availableEngine;
      }
    }
    throw buildNoAvailableEngineExceptionSupplier(actionName).get();
  }

  private Engine retrieveBookedEngine(Request request, String actionName) {
    Predicate<Engine> engineWithInformation = engine -> engine.getEndpoint().equals(request.getEndpoint());
    return getEngine(engineWithInformation, buildNoEngineWithInformationExceptionSupplier(request.getEndpoint(), actionName));
  }

  private void removeRequestAndMarkAsWaiting(Request requestToRemove, String actionName) {
    try {
      requestToRemove.safeLock();

      LOGGER.info("[{}] Engine {} in waiting mode after computing action: {}", requestToRemove.getRequestId(), requestToRemove.getEndpoint(), actionName);
      Engine bookedEngine = retrieveBookedEngine(requestToRemove, actionName);
      bookedEngine.markAsReadyForNewRequest();

      LOGGER.info("[{}] Releasing engine {} with action {}", requestToRemove.getRequestId(), requestToRemove.getEndpoint(), actionName);
      requestRegistry.release(requestToRemove.getRequestId());
    } finally {
      requestToRemove.releaseLock();
    }
  }

  private <Result> Result callOnEngine(Request request, Engine engine, String actionName, Function<Engine, Result> action) {
    try {
      LOGGER.info("[{}] Engine {} computing action: {}", request.getRequestId(), engine.getEndpoint(), actionName);

      request.safeLock();
      engine.markAsStartingAction();

      return action.apply(engine);
    } catch (Exception exception) {
      LOGGER.error("[{}] Error while launching: {} on engine {}: {}", request.getRequestId(), actionName, engine.getEndpoint(), exception.getMessage());
      throw exception;
    } finally {
      LOGGER.info("[{}] Engine {} reset to booked mode after computing action: {}", request.getRequestId(), engine.getEndpoint(), actionName);

      engine.markAsActionEnded();
      request.releaseLock();
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

  private static Supplier<NoBlockedEngineException> buildNoAvailableBlockedEngineExceptionSupplier(long requestId, String actionName) {
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
