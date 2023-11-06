package org.mlsk.service.impl.classifier.engine.mapper;

import org.mlsk.api.engine.classifier.model.ClassifierStartRequestModel;
import org.mlsk.service.model.classifier.ClassifierStartRequest;

public final class ClassifierStartRequestMapper {

  private ClassifierStartRequestMapper() {
  }

  public static ClassifierStartRequestModel toClassifierStartRequestModel(ClassifierStartRequest classifierStartRequest) {
    return new ClassifierStartRequestModel(classifierStartRequest.getRequestId(), classifierStartRequest.getPredictionColumnName(), classifierStartRequest.getActionColumnNames(), classifierStartRequest.getNumberOfValues());
  }
}
