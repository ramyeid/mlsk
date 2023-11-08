package org.mlsk.service.model.classifier;

import java.util.List;
import java.util.Objects;

import static com.google.common.collect.Lists.newArrayList;

public class ClassifierDataRequest {

  private final long requestId;
  private final String columnName;
  private final List<Integer> values;
  private final ClassifierType classifierType;

  public ClassifierDataRequest(long requestId, String columnName, List<Integer> values, ClassifierType classifierType) {
    this.requestId = requestId;
    this.columnName = columnName;
    this.values = values;
    this.classifierType = classifierType;
  }

  // Needed for deserialization from json
  public ClassifierDataRequest() {
    this(0L, null, null, null);
  }

  public long getRequestId() {
    return requestId;
  }

  public String getColumnName() {
    return columnName;
  }

  public List<Integer> getValues() {
    return values;
  }

  public ClassifierType getClassifierType() {
    return classifierType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClassifierDataRequest that = (ClassifierDataRequest) o;
    return Objects.equals(columnName, that.columnName) &&
        Objects.equals(values, that.values) &&
        Objects.equals(requestId, that.requestId) &&
        classifierType == that.classifierType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(columnName, values, requestId, classifierType);
  }

  @Override
  public String toString() {
    return "ClassifierDataRequest{" +
        "requestId=" + requestId +
        ", columnName='" + columnName + '\'' +
        ", values=" + values +
        ", classifierType=" + classifierType +
        '}';
  }
}