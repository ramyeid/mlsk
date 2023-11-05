package org.mlsk.service.model.classifier;

import java.util.Objects;

public class ClassifierCancelRequest {

  private final long requestId;

  public ClassifierCancelRequest(long requestId) {
    this.requestId = requestId;
  }

  // Needed for deserialization from json
  public ClassifierCancelRequest() {
    this(0L);
  }

  public long getRequestId() {
    return requestId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClassifierCancelRequest that = (ClassifierCancelRequest) o;
    return Objects.equals(requestId, that.requestId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId);
  }

  @Override
  public String toString() {
    return "ClassifierDropRequest{" +
        "requestId='" + requestId + '\'' +
        '}';
  }
}