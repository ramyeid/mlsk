package org.mlsk.service.impl.classifier.mapper;

import org.mlsk.api.classifier.model.ClassifierStartRequestModel;
import org.mlsk.service.model.classifier.ClassifierStartRequest;

import java.util.List;

public final class ClassifierStartRequestMapper {

  private ClassifierStartRequestMapper() {
  }

  public static ClassifierStartRequest toClassifierStartRequest(long requestId, ClassifierStartRequestModel classifierStartRequestModel) {
    String predictionColumnName = classifierStartRequestModel.getPredictionColumnName();
    List<String> actionColumnNames = classifierStartRequestModel.getActionColumnNames();
    Integer numberOfValues = classifierStartRequestModel.getNumberOfValues();

    return new ClassifierStartRequest(requestId, predictionColumnName, actionColumnNames, numberOfValues);
  }
}