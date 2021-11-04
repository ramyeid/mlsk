package org.mlsk.service.impl.orchestrator.exception;

import org.mlsk.lib.model.ServiceInformation;

import static java.lang.String.format;

public class NoAvailableEngineException extends RuntimeException {

  private NoAvailableEngineException(String message) {
    super(message);
  }

  public static NoAvailableEngineException buildNoAvailableEngineException(String actionName) {
    return new NoAvailableEngineException(format("No available engine to run %s, please try again later", actionName));
  }

  public static NoAvailableEngineException buildNoAvailableBlockedEngineException(String requestId, String actionName) {
    return new NoAvailableEngineException(format("No available engine with %s to run %s", requestId, actionName));
  }

  public static NoAvailableEngineException buildNoEngineWithInformationException(ServiceInformation serviceInformation, String actionName) {
    return new NoAvailableEngineException(format("No engine found with information %s to run %s - NOT EXPECTED - check logs!", serviceInformation, actionName));
  }
}
