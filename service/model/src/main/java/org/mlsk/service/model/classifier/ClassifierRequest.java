package org.mlsk.service.model.classifier;

import java.util.Objects;

public class ClassifierRequest {

  private final String requestId;

  public ClassifierRequest(String requestId) {
    this.requestId = requestId;
  }

  public ClassifierRequest() {
    this("");
  }

  public String getRequestId() {
    return requestId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClassifierRequest that = (ClassifierRequest) o;
    return Objects.equals(requestId, that.requestId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId);
  }

  @Override
  public String toString() {
    return "ClassifierRequest{" +
        "requestId='" + requestId + '\'' +
        '}';
  }
}
