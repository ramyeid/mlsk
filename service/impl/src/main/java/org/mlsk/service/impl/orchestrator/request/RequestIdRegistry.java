package org.mlsk.service.impl.orchestrator.request;

import org.mlsk.lib.model.ServiceInformation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class RequestIdRegistry {

  private final Map<String, ServiceInformation> serviceInformationPerRequestId;

  public RequestIdRegistry() {
    this.serviceInformationPerRequestId = new HashMap<>();
  }

  public void addRequestIdAndEngineInformation(String requestId, ServiceInformation engine) {
    serviceInformationPerRequestId.put(requestId, engine);
  }

  public Optional<ServiceInformation> getEngineInformation(String requestId) {
    return ofNullable(serviceInformationPerRequestId.get(requestId));
  }

  public void removeRequestId(String requestId) {
    serviceInformationPerRequestId.remove(requestId);
  }
}
