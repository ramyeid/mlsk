package org.mlsk.service.model.classifier;

import java.util.Objects;

public class ClassifierStartResponse {

  private final String requestId;

  public ClassifierStartResponse(String requestId) {
    this.requestId = requestId;
  }

  public ClassifierStartResponse() {
    this("");
  }

  public String getRequestId() {
    return requestId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClassifierStartResponse that = (ClassifierStartResponse) o;
    return Objects.equals(requestId, that.requestId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId);
  }

  @Override
  public String toString() {
    return "ClassifierStartResponse{" +
        "requestId='" + requestId + '\'' +
        '}';
  }
}