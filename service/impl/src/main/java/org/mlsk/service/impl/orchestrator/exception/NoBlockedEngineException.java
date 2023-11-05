package org.mlsk.service.impl.orchestrator.exception;

import static java.lang.String.format;

public class NoBlockedEngineException extends RuntimeException {

  public NoBlockedEngineException(String message) {
    super(message);
  }

  public static NoBlockedEngineException buildNoAvailableBlockedEngineException(long requestId, String actionName) {
    return new NoBlockedEngineException(format("No available engine with %d to run %s", requestId, actionName));
  }
}
