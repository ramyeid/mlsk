package org.mlsk.service.model.classifier;

import java.util.List;
import java.util.Objects;

import static com.google.common.collect.Lists.newArrayList;

public class ClassifierDataResponse {

  private final String columnName;
  private final List<Integer> values;

  public ClassifierDataResponse(String columnName, List<Integer> values) {
    this.columnName = columnName;
    this.values = values;
  }

  public ClassifierDataResponse() {
    this("", newArrayList());
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
    ClassifierDataResponse that = (ClassifierDataResponse) o;
    return Objects.equals(columnName, that.columnName) && Objects.equals(values, that.values);
  }

  @Override
  public int hashCode() {
    return Objects.hash(columnName, values);
  }

  @Override
  public String toString() {
    return "ClassifierDataResponse{" +
        "columnName='" + columnName + '\'' +
        ", values=" + values +
        '}';
  }
}