package org.mlsk.service.impl.exceptions;

public class NoAvailableEngineException extends RuntimeException {

  public NoAvailableEngineException(String actionName) {
    super(String.format("No available engine to run %s please try again later", actionName));
  }
}
