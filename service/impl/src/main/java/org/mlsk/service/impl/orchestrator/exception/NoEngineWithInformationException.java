package org.mlsk.service.impl.orchestrator.exception;

import org.mlsk.lib.model.Endpoint;

import static java.lang.String.format;

public class NoEngineWithInformationException extends RuntimeException {

  public NoEngineWithInformationException(String message) {
    super(message);
  }

  public static NoEngineWithInformationException buildNoEngineWithInformationException(Endpoint endpoint, String actionName) {
    return new NoEngineWithInformationException(format("No engine found with endpoint %s to run %s - NOT EXPECTED - check logs!", endpoint, actionName));
  }
}
