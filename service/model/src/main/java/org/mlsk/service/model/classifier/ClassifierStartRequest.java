package org.mlsk.service.model.classifier;

import java.util.List;
import java.util.Objects;

public class ClassifierStartRequest {

  private final long requestId;
  private final String predictionColumnName;
  private final List<String> actionColumnNames;
  private final int numberOfValues;
  private final ClassifierType classifierType;

  public ClassifierStartRequest(long requestId, String predictionColumnName, List<String> actionColumnNames, int numberOfValues, ClassifierType classifierType) {
    this.requestId = requestId;
    this.predictionColumnName = predictionColumnName;
    this.actionColumnNames = actionColumnNames;
    this.numberOfValues = numberOfValues;
    this.classifierType = classifierType;
  }

  // Needed for deserialization from json
  public ClassifierStartRequest() {
    this(0L, null, null, 0, null);
  }

  public long getRequestId() {
    return requestId;
  }

  public String getPredictionColumnName() {
    return predictionColumnName;
  }

  public List<String> getActionColumnNames() {
    return actionColumnNames;
  }

  public int getNumberOfValues() {
    return numberOfValues;
  }

  public ClassifierType getClassifierType() {
    return classifierType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClassifierStartRequest that = (ClassifierStartRequest) o;
    return requestId == that.requestId &&
        numberOfValues == that.numberOfValues &&
        Objects.equals(predictionColumnName, that.predictionColumnName) &&
        Objects.equals(actionColumnNames, that.actionColumnNames) &
        classifierType == that.classifierType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, predictionColumnName, actionColumnNames, numberOfValues, classifierType);
  }

  @Override
  public String toString() {
    return "ClassifierStartRequest{" +
        "requestId=" + requestId +
        ", predictionColumnName='" + predictionColumnName + '\'' +
        ", actionColumnNames=" + actionColumnNames +
        ", numberOfValues=" + numberOfValues +
        ", classifierType=" + classifierType +
        '}';
  }
}