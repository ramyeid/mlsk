package org.mlsk.service.impl.orchestrator.exception;

import org.mlsk.lib.model.ServiceInformation;

import static java.lang.String.format;

public class NoEngineWithInformationException extends RuntimeException {

  public NoEngineWithInformationException(String message) {
    super(message);
  }

  public static NoEngineWithInformationException buildNoEngineWithInformationException(ServiceInformation serviceInformation, String actionName) {
    return new NoEngineWithInformationException(format("No engine found with information %s to run %s - NOT EXPECTED - check logs!", serviceInformation, actionName));
  }
}
