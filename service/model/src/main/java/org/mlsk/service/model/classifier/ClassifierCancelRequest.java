package org.mlsk.service.model.classifier;

import java.util.Objects;

public class ClassifierCancelRequest {

  private final long requestId;
  private final ClassifierType classifierType;

  public ClassifierCancelRequest(long requestId, ClassifierType classifierType) {
    this.requestId = requestId;
    this.classifierType = classifierType;
  }

  // Needed for deserialization from json
  public ClassifierCancelRequest() {
    this(0L, null);
  }

  public long getRequestId() {
    return requestId;
  }

  public ClassifierType getClassifierType() {
    return classifierType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClassifierCancelRequest that = (ClassifierCancelRequest) o;
    return Objects.equals(requestId, that.requestId) && classifierType == that.classifierType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, classifierType);
  }

  @Override
  public String toString() {
    return "ClassifierCancelRequest{" +
        "requestId=" + requestId +
        ", classifierType=" + classifierType +
        '}';
  }
}