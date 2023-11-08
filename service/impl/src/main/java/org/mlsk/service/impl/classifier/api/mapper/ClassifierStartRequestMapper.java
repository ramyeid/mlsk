package org.mlsk.service.impl.classifier.api.mapper;

import org.mlsk.api.service.classifier.model.ClassifierStartRequestModel;
import org.mlsk.service.model.classifier.ClassifierStartRequest;
import org.mlsk.service.model.classifier.ClassifierType;

import java.util.List;

public final class ClassifierStartRequestMapper {

  private ClassifierStartRequestMapper() {
  }

  public static ClassifierStartRequest fromServiceModel(long requestId, ClassifierStartRequestModel classifierStartRequestModel, ClassifierType classifierType) {
    String predictionColumnName = classifierStartRequestModel.getPredictionColumnName();
    List<String> actionColumnNames = classifierStartRequestModel.getActionColumnNames();
    Integer numberOfValues = classifierStartRequestModel.getNumberOfValues();

    return new ClassifierStartRequest(
        requestId,
        predictionColumnName,
        actionColumnNames,
        numberOfValues,
        classifierType
    );
  }
}