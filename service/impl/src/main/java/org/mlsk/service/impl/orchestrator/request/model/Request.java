package org.mlsk.service.impl.orchestrator.request.model;

import org.mlsk.lib.model.Endpoint;

import java.util.Objects;

import static java.lang.String.valueOf;

public class Request {

  private final String action;
  private final Endpoint endpoint;

  public Request(String action, Endpoint endpoint) {
    this.action = action;
    this.endpoint = endpoint;
  }

  public String getAction() {
    return action;
  }

  public String getId() {
    return valueOf(endpoint.hashCode());
  }

  public Endpoint getEndpoint() {
    return endpoint;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Request request = (Request) o;
    return Objects.equals(action, request.action) && Objects.equals(endpoint, request.endpoint);
  }

  @Override
  public int hashCode() {
    return Objects.hash(action, endpoint);
  }

  @Override
  public String toString() {
    return "Request{" +
        "action='" + action + '\'' +
        ", endpoint=" + endpoint +
        '}';
  }
}
