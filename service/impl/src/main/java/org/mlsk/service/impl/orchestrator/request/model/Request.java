package org.mlsk.service.impl.orchestrator.request.model;

import org.mlsk.lib.model.Endpoint;

import java.util.Objects;

public class Request {

  private final long requestId;
  private final Endpoint endpoint;

  public Request(long requestId, Endpoint endpoint) {
    this.requestId = requestId;
    this.endpoint = endpoint;
  }

  public long getRequestId() {
    return requestId;
  }

  public Endpoint getEndpoint() {
    return endpoint;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Request that = (Request) o;
    return requestId == that.requestId && Objects.equals(endpoint, that.endpoint);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, endpoint);
  }

  @Override
  public String toString() {
    return "Request{" +
        "requestId=" + requestId +
        ", endpoint=" + endpoint +
        '}';
  }
}
