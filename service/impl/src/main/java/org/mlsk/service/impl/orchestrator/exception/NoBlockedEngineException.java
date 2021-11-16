package org.mlsk.service.impl.orchestrator.exception;

import static java.lang.String.format;

public class NoBlockedEngineException extends RuntimeException {

  public NoBlockedEngineException(String message) {
    super(message);
  }

  public static NoBlockedEngineException buildNoAvailableBlockedEngineException(String requestId, String actionName) {
    return new NoBlockedEngineException(format("No available engine with %s to run %s", requestId, actionName));
  }
}
