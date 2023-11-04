package org.mlsk.service.impl.orchestrator.request.registry;

import org.mlsk.lib.model.Endpoint;
import org.mlsk.service.impl.orchestrator.request.model.Request;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Optional.ofNullable;

public class RequestRegistry {

  private final Map<Long, Request> requestPerId;

  public RequestRegistry() {
    this.requestPerId = new ConcurrentHashMap<>();
  }

  public void register(long requestId, Endpoint endpoint) {
    Request request = new Request(requestId, endpoint);
    requestPerId.put(requestId, request);
  }

  public Optional<Request> get(long requestId) {
    return ofNullable(requestPerId.get(requestId));
  }

  public void remove(long requestId) {
    requestPerId.remove(requestId);
  }
}
