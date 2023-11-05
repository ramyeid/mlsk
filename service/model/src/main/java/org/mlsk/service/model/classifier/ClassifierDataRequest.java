package org.mlsk.service.model.classifier;

import java.util.List;
import java.util.Objects;

import static com.google.common.collect.Lists.newArrayList;

public class ClassifierDataRequest {

  private final long requestId;
  private final String columnName;
  private final List<Integer> values;

  public ClassifierDataRequest(long requestId, String columnName, List<Integer> values) {
    this.requestId = requestId;
    this.columnName = columnName;
    this.values = values;
  }

  // Needed for deserialization from json
  public ClassifierDataRequest() {
    this(0L, "", newArrayList());
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClassifierDataRequest that = (ClassifierDataRequest) o;
    return Objects.equals(columnName, that.columnName) && Objects.equals(values, that.values) && Objects.equals(requestId, that.requestId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(columnName, values, requestId);
  }

  @Override
  public String toString() {
    return "ClassifierDataRequest{" +
        "columnName='" + columnName + '\'' +
        ", values=" + values +
        ", requestId='" + requestId + '\'' +
        '}';
  }
}