package org.mlsk.service.impl.orchestrator.request.registry;

import org.mlsk.lib.model.Endpoint;
import org.mlsk.service.impl.orchestrator.request.model.Request;

import java.util.*;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.*;

public class RequestRegistry {

  private final Map<Long, Request> requestPerId;

  public RequestRegistry() {
    this.requestPerId = Collections.synchronizedMap(new HashMap<>());
  }

  public void register(long requestId, Endpoint endpoint) {
    Request request = new Request(requestId, endpoint);
    requestPerId.put(requestId, request);
  }

  public Optional<Request> get(long requestId) {
    return ofNullable(requestPerId.get(requestId));
  }

  public void release(long requestId) {
    requestPerId.remove(requestId);
  }

  public synchronized void releaseAll(Endpoint endpoint) {
    List<Long> engineRequestIds = requestPerId.entrySet()
        .stream()
        .filter(entrySet -> entrySet.getValue().getEndpoint().equals(endpoint))
        .map(Map.Entry::getKey)
        .collect(toList());

    engineRequestIds.forEach(this::release);
  }
}
