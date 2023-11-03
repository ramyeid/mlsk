package org.mlsk.service.impl.orchestrator.request;

import com.google.common.annotations.VisibleForTesting;
import org.mlsk.lib.model.Endpoint;
import org.mlsk.service.impl.orchestrator.request.model.Request;
import org.mlsk.service.impl.orchestrator.request.registry.RequestRegistry;

import java.util.Optional;

public class RequestHandler {

  private final RequestRegistry requestRegistry;

  public RequestHandler() {
    this(new RequestRegistry());
  }

  @VisibleForTesting
  public RequestHandler(RequestRegistry requestRegistry) {
    this.requestRegistry = requestRegistry;
  }

  public synchronized String registerNewRequest(String actionName, Endpoint endpoint) {
    Request request = new Request(actionName, endpoint);
    String requestId = request.getId();
    requestRegistry.addRequest(requestId, request);

    return requestId;
  }

  public Optional<Request> getRequest(String requestId) {
    synchronized (requestId) {
      return requestRegistry.getRequest(requestId);
    }
  }

  public void removeRequest(String requestId) {
    synchronized (requestId) {
      requestRegistry.removeRequest(requestId);
    }
  }
}
