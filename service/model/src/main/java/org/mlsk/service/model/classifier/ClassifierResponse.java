package org.mlsk.service.model.classifier;

import java.util.List;
import java.util.Objects;

import static com.google.common.collect.Lists.newArrayList;

public class ClassifierResponse {

  private final long requestId;
  private final String columnName;
  private final List<Integer> values;
  private final ClassifierType classifierType;

  public ClassifierResponse(long requestId, String columnName, List<Integer> values, ClassifierType classifierType) {
    this.requestId = requestId;
    this.columnName = columnName;
    this.values = values;
    this.classifierType = classifierType;
  }

  // Needed for deserialization from json
  public ClassifierResponse() {
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
    ClassifierResponse that = (ClassifierResponse) o;
    return requestId == that.requestId &&
        Objects.equals(columnName, that.columnName) &&
        Objects.equals(values, that.values) &&
        classifierType == that.classifierType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, columnName, values, classifierType);
  }

  @Override
  public String toString() {
    return "ClassifierResponse{" +
        "requestId=" + requestId +
        ", columnName='" + columnName + '\'' +
        ", values=" + values +
        ", classifierType=" + classifierType +
        '}';
  }
}