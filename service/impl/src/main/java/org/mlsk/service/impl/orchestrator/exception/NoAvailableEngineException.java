package org.mlsk.service.impl.orchestrator.exception;

import static java.lang.String.format;

public class NoAvailableEngineException extends RuntimeException {

  public NoAvailableEngineException(String message) {
    super(message);
  }

  public static NoAvailableEngineException buildNoAvailableEngineException(String actionName) {
    return new NoAvailableEngineException(format("No available engine to run %s, please try again later", actionName));
  }
}
