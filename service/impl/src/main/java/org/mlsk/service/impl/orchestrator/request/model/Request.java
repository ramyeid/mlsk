package org.mlsk.service.impl.orchestrator.request.model;

import org.mlsk.lib.model.ServiceInformation;

import java.util.Objects;

import static java.lang.String.valueOf;

public class Request {

  private final String action;
  private final ServiceInformation serviceInformation;

  public Request(String action, ServiceInformation serviceInformation) {
    this.action = action;
    this.serviceInformation = serviceInformation;
  }

  public String getAction() {
    return action;
  }

  public String getId() {
    return valueOf(serviceInformation.hashCode());
  }

  public ServiceInformation getServiceInformation() {
    return serviceInformation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Request request = (Request) o;
    return Objects.equals(action, request.action) && Objects.equals(serviceInformation, request.serviceInformation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(action, serviceInformation);
  }

  @Override
  public String toString() {
    return "Request{" +
        "action='" + action + '\'' +
        ", serviceInformation=" + serviceInformation +
        '}';
  }
}
