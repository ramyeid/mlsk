package org.mlsk.service.impl.orchestrator.request.registry;

import org.mlsk.service.impl.orchestrator.request.model.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class RequestRegistry {

  private final Map<String, Request> requestPerId;

  public RequestRegistry() {
    this.requestPerId = new HashMap<>();
  }

  public void addRequest(String requestId, Request request) {
    requestPerId.put(requestId, request);
  }

  public Optional<Request> getRequest(String requestId) {
    return ofNullable(requestPerId.get(requestId));
  }

  public void removeRequest(String requestId) {
    requestPerId.remove(requestId);
  }
}
