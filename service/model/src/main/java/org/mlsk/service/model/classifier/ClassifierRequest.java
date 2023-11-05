package org.mlsk.service.model.classifier;

import java.util.Objects;

public class ClassifierRequest {

  private final long requestId;

  public ClassifierRequest(long requestId) {
    this.requestId = requestId;
  }

  // Needed for deserialization from json
  public ClassifierRequest() {
    this(0L);
  }

  public long getRequestId() {
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
