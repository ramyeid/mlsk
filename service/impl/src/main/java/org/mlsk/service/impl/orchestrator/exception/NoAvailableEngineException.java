package org.mlsk.service.impl.orchestrator.exception;

import static java.lang.String.format;

public class NoAvailableEngineException extends RuntimeException {

  public NoAvailableEngineException(String actionName) {
    super(format("No available engine to run %s please try again later", actionName));
  }
}
